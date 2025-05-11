package com.monoid.hackernews.common.view.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.itemdetail.ItemDetailPane
import com.monoid.hackernews.common.view.no_story_selected
import org.jetbrains.compose.resources.stringResource

@Suppress("ComposeUnstableReceiver")
@Composable
fun StoriesDetailPane(
    itemId: ItemId?,
    onClickUrl: (Url) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (itemId == null) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = stringResource(Res.string.no_story_selected),
                modifier = Modifier.align(Alignment.Center),
            )
        }
    } else {
        ItemDetailPane(
            itemId = itemId,
            onClickUrl = onClickUrl,
            onClickUser = onClickUser,
            onClickReply = onClickReply,
            onClickLogin = onClickLogin,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
        )
    }
}
