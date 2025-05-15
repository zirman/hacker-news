package com.monoid.hackernews.common.view.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType
import com.monoid.hackernews.common.domain.util.timeBy2
import com.monoid.hackernews.common.view.itemdetail.htmlTextStyle
import com.monoid.hackernews.common.view.placeholder.PlaceholderHighlight
import com.monoid.hackernews.common.view.placeholder.placeholder
import com.monoid.hackernews.common.view.placeholder.shimmer

@Suppress("CyclomaticComplexMethod")
@Composable
fun ReplyItem(
    item: Item,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.secondary,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (item.type == ItemType.Comment) {
                        item.text ?: AnnotatedString("")
                    } else {
                        AnnotatedString(item.title.orEmpty())
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    style = LocalTextStyle.current.merge(MaterialTheme.typography.titleMedium),
                )
            }
            val itemText = item.text
            if (item.type != ItemType.Comment && itemText != null) {
                Text(
                    text = itemText,
                    modifier = Modifier
                        .padding(8.dp)
                        .placeholder(
                            visible = false,
                            color = Color.Transparent,
                            shape = MaterialTheme.shapes.small,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = LocalContentColor.current.copy(alpha = .5f),
                            ),
                        ),
                    style = htmlTextStyle().merge(MaterialTheme.typography.bodyMedium),
                )
            }
            Text(
                text = timeBy2(
                    time = item.time,
                    by = item.by,
                ),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                style = LocalTextStyle.current.merge(MaterialTheme.typography.labelMedium),
            )
        }
    }
}
