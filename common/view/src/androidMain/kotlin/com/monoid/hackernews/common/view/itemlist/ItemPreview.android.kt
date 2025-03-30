package com.monoid.hackernews.common.view.itemlist

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType

@Preview
@Composable
internal fun ItemPreview() {
    MaterialTheme {
        Item(
            item = Item(
                id = ItemId(0),
                type = ItemType.Story,
                title = "Hello World",
                text = AnnotatedString("Lorum Ipsum"),
                url = "https://www.wikipedia.com/",
                kids = emptyList(),
                upvoted = false,
                favourited = false,
                flagged = false,
                expanded = false,
                followed = false,
                by = null,
                deleted = null,
                descendants = null,
                parent = null,
                lastUpdate = null,
                score = null,
                time = null,
            ),
            onClickItem = {},
            onClickReply = {},
            onClickUser = {},
            onOpenUrl = {},
            onClickUpvote = {},
            onClickFavorite = {},
            onClickFollow = {},
            onClickFlag = {},
        )
    }
}
