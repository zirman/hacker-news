package com.monoid.hackernews.common.view.itemlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType
import com.monoid.hackernews.common.data.model.Username
import io.ktor.http.Url

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
                    by = Username("Jane Doe"),
                    descendants = 0,
                    score = 5,
                    title = "Hello World",
                    text = null,
                    url = Url("https://www.wikipedia.org/"),
                    parent = null,
                    kids = emptyList(),
                    upvoted = false,
                    favorited = false,
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
                    by = Username("Jane Doe"),
                    descendants = 0,
                    score = 5,
                    title = "Hello World",
                    text = null,
                    url = Url("https://www.wikipedia.org/"),
                    parent = null,
                    kids = emptyList(),
                    upvoted = false,
                    favorited = false,
                    flagged = false,
                    expanded = true,
                    followed = false,
                ),
            ),
            isRefreshing = false,
            onRefresh = {},
            onVisibleItem = {},
            onClickItem = {},
            onClickReply = {},
            onClickUser = {},
            onClickUrl = {},
            onClickUpvote = {},
            onClickFavorite = {},
            onClickFollow = {},
            onClickFlag = {},
            contentPadding = PaddingValues(),
            modifier = Modifier.fillMaxHeight(),
        ) {}
    }
}
