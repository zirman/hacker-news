package com.monoid.hackernews.wear.view.itemlist

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.data.ItemUi
import com.monoid.hackernews.wear.view.util.rememberAnnotatedString

@Composable
fun Item(
    itemUiState: State<ItemUi?>,
    onClickDetail: (ItemId?) -> Unit,
    modifier: Modifier = Modifier
) {
    TitleCard(
        onClick = { onClickDetail(ItemId(itemUiState.value?.item?.id!!)) },
        title = {
            val item = itemUiState.value?.item

            val title =
                rememberAnnotatedString(
                    text = item?.title ?: item?.text ?: "",
                    linkColor = LocalContentColor.current
                )

            Text(text = title)
        },
        modifier = modifier
    ) {
        val host =
            remember(itemUiState.value?.item?.url) {
                itemUiState.value?.item?.url?.let { Uri.parse(it) }?.host ?: ""
            }

        if (host.isNotBlank()) {
            Text(text = host)
        }
    }
}
