package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.fab.FabAction
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
        val (fabAction, setFabAction) = remember { mutableStateOf(FabAction.Trending) }
        fabAction.Compose(
            fabAction = fabAction,
            onClickLogin = onClickLogin,
            onClickItem = onClickItem,
            onClickReply = onClickReply,
            onClickUser = onClickUser,
            onClickUrl = onClickUrl,
            contentPadding = contentPadding,
            content = { (boxScope, hasScrolled): Pair<BoxScope, Boolean> ->
                with(boxScope) {
                    val layoutDirection = LocalLayoutDirection.current
                    StoriesFab(
                        fabAction = fabAction,
                        expanded = hasScrolled,
                        onClick = setFabAction,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                end = contentPadding.calculateEndPadding(layoutDirection),
                                bottom = contentPadding.calculateBottomPadding()
                            ),
                    )
                }
            },
        )
    }
}
