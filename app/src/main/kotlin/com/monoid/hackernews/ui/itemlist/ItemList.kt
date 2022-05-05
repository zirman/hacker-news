package com.monoid.hackernews.ui.itemlist

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.datastore.Authentication
import com.monoid.hackernews.repo.OrderedItem
import com.monoid.hackernews.settingsDataStore
import com.monoid.hackernews.ui.main.MainState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ItemList(
    mainState: MainState,
    orderedItems: State<List<OrderedItem>?>,
    selectedItem: ItemId?,
    onClickDetail: (ItemId?) -> Unit,
    onClickUpvote: (ItemId?) -> Unit,
    onClickUnUpvote: (ItemId?) -> Unit,
    onClickFavorite: (ItemId?) -> Unit,
    onClickUnFavorite: (ItemId?) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val context = LocalContext.current

    val authentication: Authentication? = remember { context.settingsDataStore.data }
        .collectAsState(initial = null)
        .value

    BoxWithConstraints(modifier = modifier) {
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
                        repeatMode = RepeatMode.Restart
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
            state = listState,
            contentPadding = WindowInsets.safeDrawing
                .only(WindowInsetsSides.Bottom)
                .asPaddingValues(),
        ) {
            items(orderedItems.value ?: emptyList(), { it.itemId.long }) { (itemId, _) ->
                LaunchedEffect(Unit) {
                    // TODO: update periodically
                    mainState.itemRepo.itemUpdate(itemId)
                }

                val itemWithKids =
                    remember {
                        mainState.itemDao.itemByIdWithKidsByIdFlow(itemId.long)
                            .distinctUntilChanged()
                    }
                        .collectAsState(initial = null)
                        .value

                Item(
                    itemWithKids = itemWithKids,
                    isUpvote = itemWithKids?.upvotes
                        ?.find { it.username == authentication?.username } != null,
                    isFavorite = itemWithKids?.favorites
                        ?.find { it.username == authentication?.username } != null,
                    isSelected = selectedItem != null && selectedItem.long == itemWithKids?.item?.id,
                    loadingBrush = if (itemWithKids == null) loadingBrush else null,
                    onClickUpvote = { onClickUpvote(it) },
                    onClickUnUpvote = { onClickUnUpvote(it) },
                    onClickFavorite = { onClickFavorite(it) },
                    onClickUnFavorite = { onClickUnFavorite(it) },
                    onClickDetail = { onClickDetail(it) },
                    onClickReply = { onClickReply(it) },
                    onClickUser = { onClickUser(it) },
                    onClickBrowser = { onClickBrowser(it) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
