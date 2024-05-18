package com.monoid.hackernews.view.home

import android.content.Context
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.adaptive.FoldAwareConfiguration
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.monoid.hackernews.MainActivity
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemListRow
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.OrderedItem
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.domain.LiveUpdateUseCase
import com.monoid.hackernews.common.ui.util.notifyInput
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.view.itemdetail.ItemDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    showItemId: ItemId?,
    itemTreeRepository: ItemTreeRepository,
    windowSizeClass: WindowSizeClass,
    orderedItemRepo: LiveUpdateUseCase<OrderedItem>,
    selectedItemId: ItemId?,
    setSelectedItemId: (ItemId?) -> Unit,
    // Used to keep track of if the story was scrolled last.
    detailInteraction: Boolean,
    setDetailInteraction: (Boolean) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
) {
    val itemRows: List<ItemListRow>? by remember {
        orderedItemRepo
            .getItems(
                TimeUnit.MINUTES
                    .toMillis(
                        context.resources.getInteger(R.integer.item_stale_minutes)
                            .toLong()
                    )
            )
            .map { orderedItems ->
                itemTreeRepository
                    .itemUiList(orderedItems.map { it.itemId })
            }
    }.collectAsStateWithLifecycle(null)

    ReportDrawnWhen { itemRows != null }

    Surface(tonalElevation = 4.dp, modifier = modifier) {
        val localItemRows = itemRows

        if (localItemRows == null) {
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
                    // TODO: actually make this refresh
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
                rememberSaveable(
                    selectedItemId,
                    saver = LazyListState.Saver
                ) { LazyListState() }

            val itemTreeRows
                by remember(selectedItemId) {
                    if (selectedItemId != null) {
                        itemTreeRepository
                            .itemUiTreeFlow(selectedItemId)
                    } else {
                        emptyFlow()
                    }
                }.collectAsStateWithLifecycle(null)

            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                if (detailInteraction && showItemId != null) {
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
                } else {
                    ItemsList(
                        listState = listState,
                        pullRefreshState = pullRefreshState,
                        itemRows = localItemRows,
                        refreshing = refreshing,
                        paddingValues = paddingValues,
                        setSelectedItemId = setSelectedItemId,
                        setDetailInteraction = setDetailInteraction,
                        onClickUser = onClickUser,
                        onClickReply = onClickReply,
                        onClickBrowser = onClickBrowser,
                        onNavigateLogin = onNavigateToLogin,
                        modifier = Modifier.notifyInput { setDetailInteraction(false) }
                    )
                }
            } else {
                val layoutDirection = LocalLayoutDirection.current
                val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()

                val twoPanePadding = PaddingValues(
                    start = safeDrawingPadding.calculateStartPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding(),
                    end = safeDrawingPadding.calculateEndPadding(layoutDirection),
                )

                val innerPadding = PaddingValues(
                    bottom = paddingValues.calculateBottomPadding(),
                )

                TwoPane(
                    first = {
                        ItemsList(
                            listState = listState,
                            pullRefreshState = pullRefreshState,
                            itemRows = localItemRows,
                            refreshing = refreshing,
                            paddingValues = innerPadding,
                            setSelectedItemId = setSelectedItemId,
                            setDetailInteraction = setDetailInteraction,
                            onClickUser = onClickUser,
                            onClickReply = onClickReply,
                            onClickBrowser = onClickBrowser,
                            onNavigateLogin = onNavigateToLogin,
                            modifier = Modifier
                                .notifyInput { setDetailInteraction(false) }
                        )
                    },
                    second = {
                        if (showItemId != null) {
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
