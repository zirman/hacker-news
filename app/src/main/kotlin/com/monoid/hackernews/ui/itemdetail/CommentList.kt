package com.monoid.hackernews.ui.itemdetail

import android.content.Context
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.R
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.api.getItem
import com.monoid.hackernews.api.toRoomItem
import com.monoid.hackernews.room.Item
import com.monoid.hackernews.room.ItemRow
import com.monoid.hackernews.room.ItemWithKids
import com.monoid.hackernews.settingsDataStore
import com.monoid.hackernews.ui.main.MainState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun CommentList(
    mainState: MainState,
    itemList: State<List<ItemRow>?>,
    updateItemWithKids: (ItemWithKids) -> Unit,
    setExpanded: (ItemId, Boolean) -> Unit,
    onClickUpvote: (ItemId) -> Unit,
    onClickUnUpvote: (ItemId) -> Unit,
    onClickFavorite: (ItemId) -> Unit,
    onClickUnFavorite: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context: Context =
        LocalContext.current

    BoxWithConstraints(modifier = modifier) {
        val coroutineScope: CoroutineScope =
            rememberCoroutineScope()

        val gradientWidth = with(LocalDensity.current) { (maxWidth * 5).toPx() }

        val loadingAnimationPosition: Float
                by rememberInfiniteTransition().animateFloat(
                    initialValue = 0f,
                    targetValue = gradientWidth,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = tween(
                            durationMillis = 1200,
                            easing = LinearEasing,
                        ),
                        repeatMode = RepeatMode.Restart,
                    ),
                )

        val loadingBrush: Brush =
            Brush.linearGradient(
                0f to Color.Transparent,
                0.1f to LocalContentColor.current.copy(alpha = 0.5f),
                0.2f to Color.Transparent,
                start = Offset(
                    x = loadingAnimationPosition,
                    y = 0f,
                ),
                end = Offset(
                    x = loadingAnimationPosition + gradientWidth,
                    y = 0f,
                ),
                tileMode = TileMode.Repeated,
            )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
                    .calculateBottomPadding() + 8.dp,
            ),
        ) {
            itemsIndexed(
                items = itemList.value ?: emptyList(),
                key = { _, itemRow -> itemRow.item.id },
                contentType = { index, _ -> index == 0 },
            ) { index, itemRow ->
                LaunchedEffect(itemRow.item.id) {
                    // item's children may not be loaded in the tree so we do that now.
                    updateItemWithKids(mainState.itemDao.itemByIdWithKidsById(itemRow.item.id)!!)

                    if (itemRow.item.lastUpdate == null ||
                        (Clock.System.now() - Instant.fromEpochSeconds(itemRow.item.lastUpdate))
                            .inWholeMinutes
                        >
                        context.resources
                            .getInteger(R.integer.item_stale_minutes)
                            .toLong()
                    ) {
                        // launch in list's coroutine scope so that the job is not
                        // canceled if the item scrolls off.
                        coroutineScope.launch {
                            try {
                                val itemFromApi =
                                    mainState.httpClient.getItem(ItemId(itemRow.item.id))

                                // insert children entries if they don't exist
                                if (itemFromApi.kids != null) {
                                    mainState.itemDao.insertIdsIgnore(
                                        itemFromApi.kids.map {
                                            Item(
                                                id = it.long,
                                                parent = itemFromApi.id.long,
                                            )
                                        }
                                    )
                                }

                                mainState.itemDao.insertReplace(itemFromApi.toRoomItem())
                                updateItemWithKids(mainState.itemDao.itemByIdWithKidsById(itemRow.item.id)!!)
                            } catch (error: Throwable) {
                                if (error is CancellationException) throw error
                            }
                        }
                    }
                }

                val isUpvoteState: State<Boolean> =
                    remember(itemRow.item.id) {
                        context.settingsDataStore.data
                            .flatMapLatest { authentication ->
                                mainState.upvoteDao.isUpvote(
                                    itemRow.item.id,
                                    if (authentication.password.isNotEmpty()) {
                                        authentication.username
                                    } else {
                                        ""
                                    }
                                )
                            }
                    }.collectAsState(initial = false)

                if (index == 0) {
                    val isFavoriteState: State<Boolean> =
                        remember(itemRow.item.id) {
                            context.settingsDataStore.data
                                .flatMapLatest { authentication ->
                                    mainState.favoriteDao.isFavorite(
                                        itemRow.item.id,
                                        if (authentication.password.isNotEmpty()) {
                                            authentication.username
                                        } else {
                                            ""
                                        }
                                    )
                                }
                        }.collectAsState(initial = false)

                    RootItem(
                        item = itemRow.item,
                        isUpvote = isUpvoteState.value,
                        isFavorite = isFavoriteState.value,
                        loadingBrush = if (itemRow.item.lastUpdate == null) loadingBrush else null,
                        onClickUpvote = { onClickUpvote(it) },
                        onClickUnUpvote = { onClickUnUpvote(it) },
                        onClickFavorite = { onClickFavorite(it) },
                        onClickUnFavorite = { onClickUnFavorite(it) },
                        onClickReply = { onClickReply(it) },
                        onClickUser = { onClickUser(it) },
                        onClickBrowser = { onClickBrowser(it) },
                    )
                } else {
                    val itemIdState: State<ItemId> =
                        rememberUpdatedState(ItemId(itemRow.item.id))

                    CommentItem(
                        commentItem = itemRow,
                        isUpvote = isUpvoteState.value,
                        loadingBrush = if (itemRow.item.lastUpdate == null) loadingBrush else null,
                        setExpanded = { setExpanded(itemIdState.value, it) },
                        onClickUser = { onClickUser(it) },
                        onClickUpvote = { onClickUpvote(it) },
                        onClickUnUpvote = { onClickUnUpvote(it) },
                        onClickReply = { onClickReply(it) },
                        modifier = Modifier
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp,
                            )
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}
