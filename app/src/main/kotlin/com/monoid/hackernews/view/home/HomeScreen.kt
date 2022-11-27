package com.monoid.hackernews.view.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import com.google.accompanist.adaptive.FoldAwareConfiguration
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.monoid.hackernews.MainActivity
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.ItemListRow
import com.monoid.hackernews.shared.data.ItemTreeRepository
import com.monoid.hackernews.shared.data.LoginAction
import com.monoid.hackernews.shared.data.OrderedItem
import com.monoid.hackernews.shared.data.Username
import com.monoid.hackernews.shared.datastore.Authentication
import com.monoid.hackernews.shared.domain.LiveUpdateUseCase
import com.monoid.hackernews.shared.view.R
import com.monoid.hackernews.view.itemdetail.ItemDetail
import com.monoid.hackernews.shared.ui.util.notifyInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    authentication: DataStore<Authentication>,
    itemTreeRepository: ItemTreeRepository,
    drawerState: DrawerState,
    windowSizeClass: WindowSizeClass,
    title: String,
    orderedItemRepo: LiveUpdateUseCase<OrderedItem>,
    snackbarHostState: SnackbarHostState,
    selectedItemId: ItemId?,
    setSelectedItemId: (ItemId?) -> Unit,
    // Used to keep track of if the story was scrolled last.
    detailInteraction: Boolean,
    setDetailInteraction: (Boolean) -> Unit,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onNavigateToUser: (Username) -> Unit,
    onNavigateToReply: (ItemId) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
    onClickUser: (Username?) -> Unit = { user ->
        if (user != null) {
            onNavigateToUser(user)
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
            val auth = authentication.data.first()

            if (auth.password.isNotEmpty()) {
                onNavigateToReply(itemId)
            } else {
                onNavigateToLogin(LoginAction.Reply(itemId.long))
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
    }
) {
    val showItemId: ItemId?
            by remember(windowSizeClass.widthSizeClass, selectedItemId, detailInteraction) {
                derivedStateOf {
                    when (windowSizeClass.widthSizeClass) {
                        WindowWidthSizeClass.Compact ->
                            if (detailInteraction) selectedItemId else null
                        else ->
                            selectedItemId
                    }
                }
            }

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            state = rememberTopAppBarState()
        )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
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
                    AnimatedVisibility(
                        visible = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded ||
                                windowSizeClass.heightSizeClass != WindowHeightSizeClass.Expanded,
                    ) {
                        val showUpButton: Boolean =
                            remember(showItemId, windowSizeClass.widthSizeClass) {
                                showItemId != null &&
                                        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                            }

                        if (showUpButton) {
                            BackHandler { setSelectedItemId(null) }

                            IconButton(onClick = { setSelectedItemId(null) }) {
                                Icon(
                                    imageVector = Icons.TwoTone.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back),
                                )
                            }
                        } else {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
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
        val itemRows: List<ItemListRow>?
                by remember {
                    orderedItemRepo
                        .getItems(
                            TimeUnit.MINUTES
                                .toMillis(
                                    context.resources.getInteger(R.integer.item_stale_minutes)
                                        .toLong()
                                )
                        )
                        .map { orderedItems ->
                            itemTreeRepository.itemUiList(orderedItems.map { it.itemId })
                        }
                }.collectAsState(initial = null)

        ReportDrawnWhen { itemRows != null }

        Surface(tonalElevation = 4.dp) {
            if (itemRows == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            } else {
                val refreshScope = rememberCoroutineScope()

                val (refreshing, setRefreshing) =
                    remember { mutableStateOf(false) }

                fun refresh() = refreshScope
                    .launch {
                        setRefreshing(true)
                        // TODO
                        delay(400)
                        setRefreshing(false)
                    }

                val pullRefreshState: PullRefreshState =
                    rememberPullRefreshState(
                        refreshing = refreshing,
                        onRefresh = ::refresh
                    )

                val listState: LazyListState =
                    rememberLazyListState()

                val detailListState: LazyListState =
                    rememberLazyListState()

                val itemTreeRows
                        by remember(selectedItemId) {
                            if (selectedItemId != null) {
                                itemTreeRepository.itemUiTreeFlow(selectedItemId)
                            } else {
                                emptyFlow()
                            }
                        }.collectAsState(initial = null)

                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                    val showItemIdPrime = showItemId

                    if (showItemIdPrime == null) {
                        ItemsList(
                            listState = listState,
                            pullRefreshState = pullRefreshState,
                            itemRows = itemRows,
                            showItemId = null,
                            refreshing = refreshing,
                            paddingValues = paddingValues,
                            setSelectedItemId = setSelectedItemId,
                            setDetailInteraction = setDetailInteraction,
                            onClickUser = onClickUser,
                            onClickReply = onClickReply,
                            onClickBrowser = onClickBrowser,
                            onNavigateLogin = onNavigateToLogin,
                        )
                    } else {
                        ItemDetail(
                            itemTreeRows = itemTreeRows,
                            paddingValues = paddingValues,
                            onClickReply = onClickReply,
                            onClickUser = onClickUser,
                            onClickBrowser = onClickBrowser,
                            onNavigateLogin = onNavigateToLogin,
                            modifier = Modifier
                                .fillMaxSize()
                                .notifyInput { setDetailInteraction(true) },
                            listState = detailListState,
                        )
                    }
                } else {
                    val layoutDirection = LocalLayoutDirection.current

                    val twoPanePadding = PaddingValues(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        top = paddingValues.calculateTopPadding(),
                        end = paddingValues.calculateEndPadding(layoutDirection),
                    )

                    val innerPadding = PaddingValues(
                        bottom = paddingValues.calculateBottomPadding(),
                    )

                    TwoPane(
                        first = {
                            ItemsList(
                                listState = listState,
                                pullRefreshState = pullRefreshState,
                                itemRows = itemRows,
                                showItemId = showItemId,
                                refreshing = refreshing,
                                paddingValues = innerPadding,
                                setSelectedItemId = setSelectedItemId,
                                setDetailInteraction = setDetailInteraction,
                                onClickUser = onClickUser,
                                onClickReply = onClickReply,
                                onClickBrowser = onClickBrowser,
                                onNavigateLogin = onNavigateToLogin,
                            )
                        },
                        second = {
                            val showItemIdPrime = showItemId

                            if (showItemIdPrime != null) {
                                ItemDetail(
                                    itemTreeRows = itemTreeRows,
                                    paddingValues = innerPadding,
                                    onClickReply = onClickReply,
                                    onClickUser = onClickUser,
                                    onClickBrowser = onClickBrowser,
                                    onNavigateLogin = onNavigateToLogin,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .notifyInput { setDetailInteraction(true) },
                                    listState = detailListState,
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .padding(innerPadding)
                                        .fillMaxSize()
                                        .notifyInput { setDetailInteraction(true) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(text = stringResource(id = R.string.no_story_selected))
                                }
                            }
                        },
                        strategy = HorizontalTwoPaneStrategy(
                            splitOffset = 360.dp,
                        ),
                        displayFeatures = calculateDisplayFeatures(
                            activity = context as MainActivity
                        ),
                        modifier = Modifier
                            .padding(twoPanePadding)
                            .fillMaxSize(),
                        foldAwareConfiguration = FoldAwareConfiguration.AllFolds,
                    )
                }
            }
        }
    }
}
