@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material.icons.twotone.Bookmarks
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.SimpleItemUiState
import com.monoid.hackernews.common.navigation.BottomNav
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.itemdetail.ItemDetail
import com.monoid.hackernews.view.itemlist.ItemsColumn
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScaffold(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentDestination by rememberSaveable { mutableStateOf(BottomNav.Stories) }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            BottomNav.entries.take(5).forEach { story ->
                item(
                    icon = {
                        Icon(
                            imageVector = if (story == currentDestination) story.selectedIcon else story.icon,
                            contentDescription = stringResource(story.contentDescription),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(story.label),
                            textAlign = TextAlign.Center,
                        )
                    },
                    selected = story == currentDestination,
                    onClick = { currentDestination = story },
                )
            }
        },
        modifier = modifier.fillMaxSize(),
    ) {
        val navigator = rememberListDetailPaneScaffoldNavigator<SimpleItemUiState>()
        BackHandler(navigator.canNavigateBack()) {
            navigator.navigateBack()
        }
        ListDetailPaneScaffold(
            directive = PaneScaffoldDirective.Default,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    ItemsColumn(
                        itemsList = uiState.itemsList,
                        onItemVisible = { item ->
                            viewModel.updateItem(item.id)
                        },
                        onItemClick = { item ->
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, item)
                        },
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    val item = navigator.currentDestination?.content ?: return@AnimatedPane
                    LifecycleEventEffect(Lifecycle.Event.ON_START) {
                        viewModel.updateItem(item.id)
                    }
                    ItemDetail(item)
                }
            },
        )
    }
}

private inline val BottomNav.icon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.TwoTone.Home
        BottomNav.Favorites -> Icons.TwoTone.Bookmarks
        BottomNav.You -> Icons.TwoTone.AccountCircle
    }

private inline val BottomNav.selectedIcon: ImageVector
    get() = when (this) {
        BottomNav.Stories -> Icons.Default.Home
        BottomNav.Favorites -> Icons.Default.Bookmarks
        BottomNav.You -> Icons.Default.AccountCircle
    }

private inline val BottomNav.contentDescription: Int
    get() = when (this) {
        BottomNav.Stories -> R.string.top_stories_description
        BottomNav.Favorites -> R.string.favorites_description
        BottomNav.You -> R.string.profile_description
    }

private inline val BottomNav.label: Int
    get() = when (this) {
        BottomNav.Stories -> R.string.top_stories
        BottomNav.Favorites -> R.string.favorites
        BottomNav.You -> R.string.profile
    }
