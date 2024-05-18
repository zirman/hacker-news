package com.monoid.hackernews.wear.view.itemlist

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemUi
import com.monoid.hackernews.util.rememberAnnotatedString

@Composable
fun Item(
    itemUi: ItemUi?,
    onClickDetail: (ItemId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    TitleCard(
        onClick = { onClickDetail(ItemId(itemUi?.item?.id!!)) },
        title = {
            val item = itemUi?.item

            val title =
                rememberAnnotatedString(
                    htmlText = item?.title ?: item?.text ?: "",
                    linkColor = LocalContentColor.current
                )

            Text(text = title)
        },
        modifier = modifier
    ) {
        val host =
            remember(itemUi?.item?.url) {
                itemUi?.item?.url?.let { Uri.parse(it) }?.host ?: ""
            }

        if (host.isNotBlank()) {
            Text(text = host)
        }
    }
}
