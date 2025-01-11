package com.monoid.hackernews.common.view.itemlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.ItemType
import com.monoid.hackernews.common.data.model.makeItem
import org.jetbrains.compose.ui.tooling.preview.Preview

actual class URL actual constructor(string: String) {
    private val url = java.net.URL(string)

    actual val host: String
        get() = url.host
}

@Preview
@Composable
private fun ItemPreview() {
    Item(
        item = makeItem(
            id = ItemId(0),
            type = ItemType.Story,
            title = AnnotatedString("Hello World"),
            text = AnnotatedString("Lorum Ipsum"),
            url = "https://www.google.com/",
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
        onClickDetail = {},
        onClickReply = {},
        onClickUser = {},
        onClickBrowser = {},
        onClickUpvote = {},
        onClickFavorite = {},
        onClickFollow = {},
        onClickFlag = {},
    )
}
