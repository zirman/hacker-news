@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.monoid.hackernews.common.view.stories

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.TrendingUp
import androidx.compose.material.icons.twotone.Feedback
import androidx.compose.material.icons.twotone.Forum
import androidx.compose.material.icons.twotone.RssFeed
import androidx.compose.material.icons.twotone.Whatshot
import androidx.compose.material.icons.twotone.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.ask
import com.monoid.hackernews.common.view.hot
import com.monoid.hackernews.common.view.jobs
import com.monoid.hackernews.common.view.new
import com.monoid.hackernews.common.view.show
import com.monoid.hackernews.common.view.trending
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun StoriesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (fabAction, setFabAction) = rememberSaveable { mutableStateOf(FabAction.Trending) }
    val storiesViewModel = createStoriesViewModel(fabAction.storyOrdering)
    Box(modifier = modifier) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            storiesViewModel.events.collect { event ->
                when (event) {
                    is StoriesViewModel.Event.Error -> {
                        Toast
                            .makeText(
                                context,
                                "An error occurred: ${event.message}",
                                Toast.LENGTH_SHORT,
                            )
                            .show()
                    }

                    is StoriesViewModel.Event.NavigateLogin -> {
                        onClickLogin()
                    }
                }
            }
        }
        val (loading, itemsList) = storiesViewModel.uiState.collectAsStateWithLifecycle().value
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                val scope = rememberCoroutineScope()
                StoriesListPane(
                    listState = storiesViewModel.listState,
                    itemsList = itemsList,
                    onVisibleItem = storiesViewModel::updateItem,
                    onClickItem = { item ->
                        scope.launch {
                            navigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = "${item.id.long}",
                            )
                        }
                    },
                    onClickReply = {},
                    onClickUser = {},
                    onClickUrl = onClickUrl,
                    onClickUpvote = storiesViewModel::toggleUpvoted,
                    onClickFavorite = {},
                    onClickFollow = {},
                    onClickFlag = {},
                    contentPadding = listContentPadding(),
                )
            },
            detailPane = {
                val itemId = (navigator.currentDestination?.contentKey as? String)
                    ?.toLong()
                    ?.let { ItemId(it) }
                StoriesDetailPane(
                    itemId = itemId,
                    onClickUrl = onClickUrl,
                )
            },
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        StoriesFab(
            fabAction = fabAction,
            expanded = storiesViewModel.listState.expandedFab(),
            onClick = setFabAction,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
fun StoriesFab(
    fabAction: FabAction,
    expanded: Boolean,
    onClick: (FabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            ExtendedFloatingActionButton(
                onClick = { fabMenuExpanded = fabMenuExpanded.not() },
                expanded = expanded || fabMenuExpanded,
                icon = { Icon(fabAction.icon, null) },
                text = { Text(stringResource(fabAction.text)) },
            )
        },
        modifier = modifier,
    ) {
        for (i in FabAction.entries.indices) {
            val item = FabAction.entries[i]
            FloatingActionButtonMenuItem(
                modifier = Modifier.semantics {
                    isTraversalGroup = true
                    // Add a custom a11y action to allow closing the menu when focusing
                    // the last menu item, since the close button comes before the first
                    // menu item in the traversal order.
                    if (i == FabAction.entries.size - 1) {
                        customActions = listOf(
                            CustomAccessibilityAction(
                                label = "Close menu",
                                action = {
                                    fabMenuExpanded = false
                                    true
                                },
                            )
                        )
                    }
                },
                onClick = {
                    onClick(item)
                    fabMenuExpanded = false
                },
                icon = { Icon(item.icon, contentDescription = null) },
                text = { Text(text = stringResource(item.text)) },
            )
        }
    }
}

enum class FabAction(
    val icon: ImageVector,
    val text: StringResource,
    val storyOrdering: StoryOrdering,
) {
    // TODO: post
    // Post(Icons.TwoTone.Add, Res.string.post),
    Jobs(Icons.TwoTone.Work, Res.string.jobs, StoryOrdering.Jobs),
    Ask(Icons.TwoTone.Forum, Res.string.ask, StoryOrdering.Ask),
    Show(Icons.TwoTone.Feedback, Res.string.show, StoryOrdering.Show),
    Hot(Icons.TwoTone.Whatshot, Res.string.hot, StoryOrdering.Hot),
    New(Icons.TwoTone.RssFeed, Res.string.new, StoryOrdering.New),
    Trending(Icons.AutoMirrored.TwoTone.TrendingUp, Res.string.trending, StoryOrdering.Trending),
}

fun LazyListState.expandedFab(): Boolean =
    firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

@Composable
fun listContentPadding(): PaddingValues {
    val density = LocalDensity.current
    return with(density) {
        val safeDrawing = WindowInsets.safeDrawing
        val layoutDirection = LocalLayoutDirection.current
        WindowInsets(
            top = safeDrawing.getTop(density),
            // TODO: ignore safe drawing bottom if using bottom nav
            bottom = safeDrawing.getBottom(density).coerceAtLeast(
                // height of fab
                80.dp.toPx().roundToInt()
            ),
            left = safeDrawing.getLeft(density, layoutDirection),
            right = safeDrawing.getRight(density, layoutDirection),
        )
    }.asPaddingValues()
}
