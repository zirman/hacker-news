package com.monoid.hackernews.common.view.itemlist

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun ItemsColumnPreview() {
    MaterialTheme {
        ItemsColumn(
            itemsList = listOf(
                Item(
                    id = ItemId(0),
                    lastUpdate = 123,
                    type = ItemType.Story,
                    time = 123,
                    deleted = false,
                    by = "Jane Doe",
                    descendants = 0,
                    score = 5,
                    title = AnnotatedString("Hello World"),
                    text = null,
                    url = "https://www.wikipedia.org/",
                    parent = null,
                    kids = emptyList(),
                    upvoted = false,
                    favourited = false,
                    flagged = false,
                    expanded = true,
                    followed = false,
                ),
                Item(
                    id = ItemId(1),
                    lastUpdate = 123,
                    type = ItemType.Story,
                    time = 123,
                    deleted = false,
                    by = "Jane Doe",
                    descendants = 0,
                    score = 5,
                    title = AnnotatedString("Hello World"),
                    text = null,
                    url = "https://www.wikipedia.org/",
                    parent = null,
                    kids = emptyList(),
                    upvoted = false,
                    favourited = false,
                    flagged = false,
                    expanded = true,
                    followed = false,
                ),
            ),
            onVisibleItem = {},
            onClickItem = {},
            onClickReply = {},
            onClickUser = {},
            onOpenUrl = {},
            onClickUpvote = {},
            onClickFavorite = {},
            onClickFollow = {},
            onClickFlag = {},
            listState = rememberLazyListState(),
            modifier = Modifier.fillMaxHeight(),
        )
    }
}
