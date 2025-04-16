package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Reply
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Quickreply
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.Flag
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material.icons.twotone.Quickreply
import androidx.compose.material.icons.twotone.ThumbUp
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.LoginAction
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.util.rememberTimeBy
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.deleted
import com.monoid.hackernews.common.view.flag
import com.monoid.hackernews.common.view.follow
import com.monoid.hackernews.common.view.more_options
import com.monoid.hackernews.common.view.reply
import com.monoid.hackernews.common.view.text.ClickableTextBlock
import com.monoid.hackernews.common.view.un_flag
import com.monoid.hackernews.common.view.un_vote
import com.monoid.hackernews.common.view.unfollow
import com.monoid.hackernews.common.view.upvote
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("CyclomaticComplexMethod")
@Composable
fun ItemComment(
    threadItem: ItemDetailViewModel.ThreadItemUiState,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickLogin: (LoginAction) -> Unit,
    onVisible: (ItemId) -> Unit,
    onClick: (ItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val item = threadItem.item
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(item.id) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                onVisible(item.id)
                delay(5.toDuration(DurationUnit.SECONDS))
            }
        }
    }
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clickable { onClick(item.id) }
            .animateContentSize(),
    ) {
        ThreadDepth(threadItem.depth)
        Surface(tonalElevation = (threadItem.descendants * 4).dp) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val timeByUserAnnotatedString: AnnotatedString =
                        rememberTimeBy(time = item.time, by = item.by)
                    ClickableTextBlock(
                        text = timeByUserAnnotatedString,
                        lines = 1,
//                    onClick = { offset ->
//                        if (itemUiState.value?.itemUi?.isExpanded == true) {
//                            val username: Username? = timeByUserAnnotatedString.value
//                                .getStringAnnotations(
//                                    tag = userTag,
//                                    start = offset,
//                                    end = offset
//                                )
//                                .firstOrNull()
//                                ?.item
//                                ?.let { Username(it) }
//
//                            if (username != null) {
//                                onClickUser(username)
//                            } else {
//                                coroutineScope.launch {
//                                    itemUiState.value?.itemUi?.toggleExpanded()
//                                }
//                            }
//                        } else {
//                            coroutineScope.launch {
//                                itemUiState.value?.itemUi?.toggleExpanded()
//                            }
//                        }
//                    },
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp)
                            .align(Alignment.Top),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LocalContentColor.current,
                        ),
                    )
                    if (item.expanded.not()) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(4.dp),
                        ) {
                            Text(
                                text = "${threadItem.descendants} responses",
                                maxLines = 1,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box {
                        val (expanded: Int, setContextExpanded) = remember {
                            mutableStateOf(0)
                        }
                        IconButton(onClick = { setContextExpanded(1) }) {
                            Icon(
                                imageVector = Icons.TwoTone.MoreVert,
                                contentDescription = stringResource(Res.string.more_options),
                            )
                        }
                        DropdownMenu(
                            expanded = expanded != 0,
                            onDismissRequest = { setContextExpanded(0) },
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(Res.string.reply)) },
                                onClick = {
//                                itemUi.id.let { onClickReply(ItemId(it)) }
                                    setContextExpanded(0)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.TwoTone.Reply,
                                        contentDescription = stringResource(Res.string.reply),
                                    )
                                },
                            )
                            DropdownMenuItem(text = {
                                Text(
                                    text = stringResource(
                                        if (item.upvoted == true) {
                                            Res.string.un_vote
                                        } else {
                                            Res.string.upvote
                                        },
                                    ),
                                )
                            }, onClick = {
//                                coroutineScope.launch {
//                                    itemUi?.itemUi?.toggleUpvote(onNavigateLogin = onNavigateLogin)
//                                }

                                setContextExpanded(0)
                            }, leadingIcon = {
                                Icon(
                                    imageVector = if (item.upvoted == true) {
                                        Icons.Filled.ThumbUp
                                    } else {
                                        Icons.TwoTone.ThumbUp
                                    },
                                    contentDescription = stringResource(
                                        if (item.upvoted == true) {
                                            Res.string.un_vote
                                        } else {
                                            Res.string.upvote
                                        },
                                    ),
                                )
                            })
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            if (item.followed) {
                                                Res.string.unfollow
                                            } else {
                                                Res.string.follow
                                            },
                                        )
                                    )
                                },
                                onClick = {
//                                coroutineScope.launch {
//                                    itemUi.toggleFollowed()
//                                }

                                    setContextExpanded(0)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (item.followed) {
                                            Icons.Filled.Quickreply
                                        } else {
                                            Icons.TwoTone.Quickreply
                                        },
                                        contentDescription = stringResource(
                                            if (item.followed) {
                                                Res.string.unfollow
                                            } else {
                                                Res.string.follow
                                            },
                                        ),
                                    )
                                },
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            if (item.flagged == true) {
                                                Res.string.un_flag
                                            } else {
                                                Res.string.flag
                                            },
                                        ),
                                    )
                                },
                                onClick = {
//                                coroutineScope.launch {
//                                    itemUi.toggleFlag(onNavigateLogin)
//                                }

                                    setContextExpanded(0)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (item.flagged == true) {
                                            Icons.Filled.Flag
                                        } else {
                                            Icons.TwoTone.Flag
                                        },
                                        contentDescription = stringResource(
                                            if (item.flagged == true) {
                                                Res.string.un_flag
                                            } else {
                                                Res.string.flag
                                            },
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
                if (item.expanded) {
                    Text(
                        text = if (item.deleted == true) {
                            AnnotatedString(stringResource(Res.string.deleted))
                        } else {
                            item.text ?: AnnotatedString("")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ThreadDepth(depth: Int, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    // TODO: make configurable
    val threadGap = 16.dp
    Canvas(modifier = modifier.fillMaxHeight().width(threadGap * (depth - 1))) {
        val threadGapPx = threadGap.toPx()
        val thicknessPx = 15.dp.toPx()
        val thicknessPx2 = thicknessPx / 2
        for (d in 1..<depth) {
            val x = d * threadGapPx - thicknessPx2
            drawLine(
                color = when ((d - 1).mod(3)) {
                    0 -> primary
                    1 -> secondary
                    else -> tertiary
                },
                strokeWidth = thicknessPx,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
            )
        }
    }
}
