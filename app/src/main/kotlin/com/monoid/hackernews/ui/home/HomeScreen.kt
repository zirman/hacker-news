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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
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
import com.monoid.hackernews.repo.ItemRepo
import com.monoid.hackernews.repo.OrderedItemRepo
import com.monoid.hackernews.ui.itemdetail.ItemDetail
import com.monoid.hackernews.ui.itemlist.ItemList
import com.monoid.hackernews.ui.util.WindowSize
import com.monoid.hackernews.ui.util.WindowSizeClass
import com.monoid.hackernews.ui.util.networkConnectivity
import com.monoid.hackernews.ui.util.notifyInput
import com.monoid.hackernews.ui.util.runWhen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    drawerState: DrawerState,
    mainNavController: NavController,
    windowSize: WindowSize,
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
        mainNavController.navigate(MainNavigation.Reply.routeWithArgs(itemId))
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
) {
    val lifecycleOwner: LifecycleOwner =
        LocalLifecycleOwner.current

    val lastUpdateState =
        rememberSaveable { mutableStateOf<Long?>(null) }

    fun setLastUpdate(epochMillis: Long) {
        lastUpdateState.value = epochMillis
    }

    // refreshes if last update is stale on resume and loops refresh.
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            context.networkConnectivity().runWhen({ it }) {
                while (true) {
                    var lastUpdate = lastUpdateState.value

                    if (
                        lastUpdate == null ||
                        Clock.System.now().toEpochMilliseconds() - lastUpdate >=
                        TimeUnit.MINUTES.toMillis(
                            context.resources.getInteger(R.integer.item_stale_minutes).toLong()
                        )
                    ) {
                        orderedItemRepo.updateRepoItems()
                        lastUpdate = Clock.System.now().toEpochMilliseconds()
                        setLastUpdate(lastUpdate)
                    }

                    delay(
                        (
                            TimeUnit.MINUTES.toMillis(
                                context.resources.getInteger(R.integer.item_stale_minutes)
                                    .toLong()
                            ) + lastUpdate
                            ) - Clock.System.now().toEpochMilliseconds()
                    )
                }
            }
        }
    }

    // layout to provide system bar scrims on all sides
    val scrollBehavior: TopAppBarScrollBehavior =
        remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }

    val showItemId: ItemId? =
        remember(windowSize.width, selectedItemId, detailInteraction) {
            when (windowSize.width) {
                WindowSizeClass.Compact ->
                    if (detailInteraction) selectedItemId else null
                WindowSizeClass.Medium ->
                    selectedItemId
                WindowSizeClass.Expanded ->
                    selectedItemId
            }
        }

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
                    AnimatedVisibility(visible = windowSize.width != WindowSizeClass.Expanded) {
                        if (showItemId != null && windowSize.width == WindowSizeClass.Compact) {
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

        val itemRows: State<List<ItemRepo.ItemRow>?> =
            remember {
                orderedItemRepo.getRepoItems()
                    .map { orderedItems ->
                        mainViewModel.itemRepo.itemUiList(orderedItems.map { it.itemId })
                    }
            }
                .collectAsState(initial = null)

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
                    val (job: Job?, setJob) =
                        remember { mutableStateOf<Job?>(null) }

                    val listState: LazyListState =
                        rememberLazyListState()

                    if (showItemId == null || windowSize.width != WindowSizeClass.Compact) {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = {
                                if (job?.isCompleted != false) {
                                    setJob(
                                        coroutineScope.launch {
                                            swipeRefreshState.isRefreshing = true

                                            try {
                                                orderedItemRepo.updateRepoItems()

                                                setLastUpdate(
                                                    Clock.System.now().toEpochMilliseconds()
                                                )
                                            } finally {
                                                swipeRefreshState.isRefreshing = false
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = when (windowSize.width) {
                                WindowSizeClass.Compact ->
                                    Modifier.weight(1f)
                                WindowSizeClass.Medium,
                                WindowSizeClass.Expanded ->
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
                        (detailInteraction || windowSize.width != WindowSizeClass.Compact)
                    ) {
                        key(showItemId) {
                            ItemDetail(
                                itemId = showItemId,
                                onClickReply = onClickReply,
                                onClickUser = onClickUser,
                                onClickBrowser = onClickBrowser,
                                modifier = modifier.notifyInput { setDetailInteraction(true) },
                                listState = detailListState,
                            )
                        }
                    } else if (windowSize.width != WindowSizeClass.Compact) {
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
