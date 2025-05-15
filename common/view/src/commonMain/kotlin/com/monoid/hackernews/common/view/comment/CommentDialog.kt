package com.monoid.hackernews.common.view.comment

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.cancel
import com.monoid.hackernews.common.view.reply
import com.monoid.hackernews.common.view.send
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun CommentDialog(
    parentId: ItemId,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommentViewModel = createCommentViewModel(parentId = parentId),
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(),
) {
    val compact = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
    val (item, loading, text) = viewModel.uiState.collectAsStateWithLifecycle().value
    Card(modifier = modifier.fillMaxSize()) {
        if (compact.not()) {
            if (item != null) {
                ReplyItem(
                    item = item,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f),
                )
            }
        }
        TextField(
            text,
            enabled = loading.not(),
            onValueChange = viewModel::updateComment,
            label = { Text(stringResource(Res.string.reply)) },
            trailingIcon = if (compact) {
                {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(onClick = viewModel::sendComment) {
                            Icon(
                                Icons.AutoMirrored.TwoTone.Send,
                                contentDescription = stringResource(Res.string.send)
                            )
                        }
                    }
                }
            } else {
                null
            },
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
        )
        if (compact.not()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onDismiss, enabled = loading.not()) {
                    Text(stringResource(Res.string.cancel))
                }
                TextButton(onClick = viewModel::sendComment, enabled = loading.not()) {
                    Text(stringResource(Res.string.send))
                    AnimatedVisibility(loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(start = 16.dp).size(24.dp),
                        )
                    }
                }
            }
        }
    }
}
