package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import io.ktor.http.Url

@Suppress("ComposeUnstableReceiver")
@Composable
fun StoriesListPane(
    onClickItem: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickLogin: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        // TODO: submit issue tracker for this not updating
        // val (fabAction, setFabAction) = remember { mutableStateOf(FabAction.Trending) }
        var fabAction by remember { mutableStateOf(FabAction.Trending) }
        val movableContent = remember {
            movableContentOf { (boxScope, hasScrolled): Pair<BoxScope, Boolean> ->
                with(boxScope) {
                    StoriesFab(
                        fabAction = fabAction,
                        expanded = hasScrolled,
                        onClick = { fabAction = it },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(contentPadding),
                    )
                }
            }
        }
        // This contrived composable is so that lazyListState scroll position resets to the top when
        // switching story ordering
        fabAction.Compose(
            fabAction = fabAction,
            onClickLogin = onClickLogin,
            onClickItem = onClickItem,
            onClickReply = onClickReply,
            onClickUser = onClickUser,
            onClickUrl = onClickUrl,
            contentPadding = contentPadding,
            content = movableContent,
        )
    }
}
