package com.monoid.hackernews.wear.view.itemlist

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemUi

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

            val htmlString = item?.title ?: item?.text ?: ""
            Text(
                text = remember(htmlString) { AnnotatedString.fromHtml(htmlString = htmlString) },
            )
        },
        modifier = modifier,
    ) {
        val host = remember(itemUi?.item?.url) {
            itemUi?.item?.url?.let { Uri.parse(it) }?.host ?: ""
        }

        if (host.isNotBlank()) {
            Text(text = host)
        }
    }
}
