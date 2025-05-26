package com.monoid.hackernews.common.view.comment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.an_error_occurred
import com.monoid.hackernews.common.view.cancel
import com.monoid.hackernews.common.view.platform.PlatformLoadingIndicator
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
    var erred by rememberSaveable { mutableStateOf(false) }
    val (item, loading, text) = viewModel.uiState.collectAsStateWithLifecycle().value
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            for (event in viewModel.events) {
                when (event) {
                    CommentViewModel.Event.CloseComment -> {
                        onDismiss()
                    }

                    CommentViewModel.Event.Error -> {
                        erred = true
                    }
                }
            }
        }
    }
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
            isError = erred,
            onValueChange = viewModel::updateComment,
            label = { Text(stringResource(Res.string.reply)) },
            trailingIcon = if (compact) {
                {
                    if (loading) {
                        PlatformLoadingIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(
                            onClick = {
                                erred = false
                                viewModel.sendComment()
                            },
                        ) {
                            Icon(
                                Icons.AutoMirrored.TwoTone.Send,
                                contentDescription = stringResource(Res.string.send),
                            )
                        }
                    }
                }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth(),
        )
        AnimatedVisibility(erred) {
            Text(
                stringResource(Res.string.an_error_occurred),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            )
        }
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
                        PlatformLoadingIndicator(
                            modifier = Modifier.padding(start = 16.dp).size(24.dp),
                        )
                    }
                }
            }
        }
    }
}
