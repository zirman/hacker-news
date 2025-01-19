@file:OptIn(ExperimentalSplitPaneApi::class)

package com.monoid.hackernews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.data.Uri
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.domain.navigation.Route
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.itemlist.ItemsColumn
import com.monoid.hackernews.common.view.main.LoginDialog
import com.monoid.hackernews.common.view.main.openWebpage
import com.monoid.hackernews.common.view.stories.StoriesViewModel
import com.monoid.hackernews.common.view.stories.createStoriesViewModel
import com.monoid.hackernews.common.view.theme.AppTheme
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import java.awt.Cursor

fun main() {
    startKoin {
        modules(ApplicationModule().module)
    }

    application {
        Window(onCloseRequest = ::exitApplication) {
            KoinContext {
                AppTheme {
                    var showLoginDialog by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    if (showLoginDialog != 0) {
                        LoginDialog(onDismissRequest = { showLoginDialog = 0 })
                    }
                    Scaffold(
                        bottomBar = {
                            BottomAppBar(
                                actions = {
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            contentDescription = "Localized description",
                                        )
                                    }
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Mic,
                                            contentDescription = "Localized description",
                                        )
                                    }
                                    IconButton(onClick = { /* do something */ }) {
                                        Icon(
                                            Icons.Filled.Image,
                                            contentDescription = "Localized description",
                                        )
                                    }
                                },
                                floatingActionButton = {
                                    FloatingActionButton(
                                        onClick = { /* do something */ },
                                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    ) {
                                        Icon(Icons.Filled.Add, "Localized description")
                                    }
                                }
                            )
                        },
                    ) { innerPadding ->
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = Route.Home,
                            modifier = Modifier.padding(innerPadding),
                        ) {
                            composable<Route.Home> {
                                HNPanes(
                                    onOpenLogin = {
                                        showLoginDialog = 1
                                    },
                                    onOpenBrowser = {
                                        openWebpage(Uri(checkNotNull(it.url)))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HNPanes(
    onOpenLogin: () -> Unit,
    onOpenBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    val splitterState = rememberSplitPaneState()
    var itemId by rememberSaveable {
        mutableLongStateOf(-1L)
    }
    HorizontalSplitPane(
        splitPaneState = splitterState,
        modifier = modifier,
    ) {
        first(640.dp) {
            val viewModel: StoriesViewModel = createStoriesViewModel(key = "default")
            val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
            ItemsColumn(
                listState = viewModel.listState,
                itemsList = uiState.itemsList,
                onVisibleItem = { viewModel.updateItem(it.id) },
                onClickItem = {
                    itemId = it.id.long
                },
                onClickReply = { },
                onClickUser = { },
                onOpenUrl = onOpenBrowser,
                onClickUpvote = { },
                onClickFavorite = { },
                onClickFollow = { },
                onClickFlag = { },
                modifier = Modifier.fillMaxHeight(),
            )
        }
        second(640.dp) {
            if (itemId != -1L) {
                ItemDetailPane(
                    itemId = ItemId(itemId),
                    onOpenBrowser = onOpenBrowser,
                )
            }
        }
        splitter {
            visiblePart {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background),
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                        .background(SolidColor(Color.Gray), alpha = 0.50f)
                        .width(9.dp)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
