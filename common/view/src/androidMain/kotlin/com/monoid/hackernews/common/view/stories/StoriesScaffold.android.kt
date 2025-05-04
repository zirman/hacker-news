@file:OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoriesScaffold(
    navigator: ThreePaneScaffoldNavigator<Any>,
    onClickLogin: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                val scope = rememberCoroutineScope()
                StoriesListPane(
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
                    onClickUrl = {},
                    onClickFavorite = {},
                    onClickFollow = {},
                    onClickFlag = {},
                    onClickLogin = onClickLogin,
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
