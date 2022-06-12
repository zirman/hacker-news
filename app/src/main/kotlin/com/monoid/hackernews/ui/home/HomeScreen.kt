package com.monoid.hackernews.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.monoid.hackernews.MainNavigation
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.R
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.navigation.LoginAction
import com.monoid.hackernews.repo.ItemListRow
import com.monoid.hackernews.repo.OrderedItemRepo
import com.monoid.hackernews.settingsDataStore
import com.monoid.hackernews.ui.itemdetail.ItemDetail
import com.monoid.hackernews.ui.itemlist.ItemList
import com.monoid.hackernews.ui.util.notifyInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    drawerState: DrawerState,
    mainNavController: NavController,
    windowSizeClass: WindowSizeClass,
    title: String,
    orderedItemRepo: OrderedItemRepo,
    snackbarHostState: SnackbarHostState,
    selectedItemId: ItemId?,
    setSelectedItemId: (ItemId?) -> Unit,
    // Used to keep track of if the story was scrolled last.
    detailInteraction: Boolean,
    setDetailInteraction: (Boolean) -> Unit,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onClickUser: (Username?) -> Unit = { user ->
        if (user != null) {
            mainNavController.navigate(MainNavigation.User.routeWithArgs(user))
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.url_is_null),
                Toast.LENGTH_SHORT
            ).show()
        }
    },
    onClickReply: (ItemId) -> Unit = { itemId ->
        coroutineScope.launch {
            val authentication = context.settingsDataStore.data.first()

            if (authentication.password.isNotEmpty()) {
                mainNavController.navigate(MainNavigation.Reply.routeWithArgs(itemId))
            } else {
                mainNavController.navigate(
                    MainNavigation.Login.routeWithArgs(LoginAction.Reply(itemId.long))
                )
            }
        }
    },
    onClickBrowser: (String?) -> Unit = { url ->
        val uri = url?.let { Uri.parse(it) }

        if (uri != null) {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.url_is_null),
                Toast.LENGTH_SHORT
            ).show()
        }
    },
    onNavigateLogin: (LoginAction) -> Unit = { loginAction ->
        mainNavController.navigate(MainNavigation.Login.routeWithArgs(loginAction))
    },
) {
    val showItemId: ItemId? =
        remember(windowSizeClass.widthSizeClass, selectedItemId, detailInteraction) {
            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact ->
                    if (detailInteraction) selectedItemId else null
                else ->
                    selectedItemId
            }
        }

    // enterAlwaysScrollBehavior is buggy
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(
            state = rememberTopAppBarScrollState(),
        )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.hacker_news))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                },
                navigationIcon = {
                    AnimatedVisibility(visible = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded) {
                        if (showItemId != null && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                            BackHandler { setSelectedItemId(null) }

                            IconButton(onClick = { setSelectedItemId(null) }) {
                                Icon(
                                    imageVector = Icons.TwoTone.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back),
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
                            ) {
                                Icon(
                                    imageVector = Icons.TwoTone.Menu,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    navigationIconContentColor = MaterialTheme.colorScheme.tertiary,
                    titleContentColor = MaterialTheme.colorScheme.tertiary,
                    actionIconContentColor = MaterialTheme.colorScheme.tertiary,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeContent.only(WindowInsetsSides.Bottom)
                ),
            )
        },
    ) { paddingValues ->
        val swipeRefreshState: SwipeRefreshState =
            rememberSwipeRefreshState(isRefreshing = false)

        val itemRows: State<List<ItemListRow>?> =
            remember {
                orderedItemRepo.getItems(context).map { orderedItems ->
                    mainViewModel.itemRepo.itemUiList(orderedItems.map { it.itemId })
                }
            }.collectAsState(initial = null)

        val loadingState: State<Boolean> =
            remember(itemRows.value) { derivedStateOf { itemRows.value == null } }

        Surface(
            modifier = Modifier.padding(paddingValues = paddingValues),
            tonalElevation = 4.dp,
        ) {
            if (loadingState.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            } else {
                Row {
                    val listState: LazyListState =
                        rememberLazyListState()

                    if (showItemId == null || windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = { /* TODO */ },
                            modifier = when (windowSizeClass.widthSizeClass) {
                                WindowWidthSizeClass.Compact ->
                                    Modifier.weight(1f)
                                else ->
                                    Modifier.width(320.dp)
                            }
                                .fillMaxHeight()
                                .notifyInput { setDetailInteraction(false) },
                            indicator = { state, trigger ->
                                SwipeRefreshIndicator(
                                    state = state,
                                    refreshTriggerDistance = trigger,
                                    scale = true,
                                )
                            },
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides MaterialTheme.colorScheme.primary,
                            ) {
                                ItemList(
                                    itemRows = itemRows,
                                    selectedItem = showItemId,
                                    onClickDetail = {
                                        setDetailInteraction(true)
                                        setSelectedItemId(it)
                                    },
                                    onClickUser = onClickUser,
                                    onClickReply = onClickReply,
                                    onClickBrowser = onClickBrowser,
                                    onNavigateLogin = onNavigateLogin,
                                    listState = listState,
                                )
                            }
                        }
                    }

                    val modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()

                    val detailListState: LazyListState =
                        rememberLazyListState()

                    if (
                        showItemId != null &&
                        (detailInteraction || windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact)
                    ) {
                        key(showItemId) {
                            ItemDetail(
                                itemId = showItemId,
                                onClickReply = onClickReply,
                                onClickUser = onClickUser,
                                onClickBrowser = onClickBrowser,
                                onNavigateLogin = onNavigateLogin,
                                modifier = modifier.notifyInput { setDetailInteraction(true) },
                                listState = detailListState,
                            )
                        }
                    } else if (windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact) {
                        Box(
                            modifier = modifier,
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = stringResource(id = R.string.no_story_selected))
                        }
                    }
                }
            }
        }
    }
}
