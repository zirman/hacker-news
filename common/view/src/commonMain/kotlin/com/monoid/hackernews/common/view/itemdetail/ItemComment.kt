package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.view.html.rememberAnnotatedHtmlString
import com.monoid.hackernews.common.view.theme.LocalCommentIndentation

@Composable
fun ItemComment(
    threadItem: ItemDetailViewModel.ThreadItemUiState,
    onVisible: (ItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val item = threadItem.item
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(item.id) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            onVisible(item.id)
        }
    }
    Box(
        modifier = modifier
            .height(IntrinsicSize.Min),
    ) {
        Surface {
            Text(
                text = rememberAnnotatedHtmlString(item.text.orEmpty()),
                modifier = Modifier.padding(horizontal = 16.dp),
                overflow = TextOverflow.Ellipsis,
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        textIndent = TextIndent(
                            firstLine = LocalCommentIndentation.current.em,
                        ),
                    ),
                ),
            )
        }
    }
}
