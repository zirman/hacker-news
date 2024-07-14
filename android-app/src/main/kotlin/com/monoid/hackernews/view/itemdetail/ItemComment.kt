package com.monoid.hackernews.view.itemdetail

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
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.ui.text.ClickableTextBlock
import com.monoid.hackernews.common.ui.util.rememberTimeBy
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.view.rememberAnnotatedHtmlString
import com.monoid.hackernews.view.theme.LocalCommentIndentation
import kotlinx.coroutines.delay

@Composable
fun ItemComment(
    threadItem: ItemDetailViewModel.ThreadItemUiState,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
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
                delay(timeMillis = 5_000)
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
        Surface(tonalElevation = (threadItem.decendents * 4).dp) {
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
                        overflow = TextOverflow.Ellipsis,
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
                                text = "${threadItem.decendents} responses",
                                maxLines = 1,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box {
                        val (expanded: Boolean, setContextExpanded) = remember {
                            mutableStateOf(
                                false
                            )
                        }

                        IconButton(onClick = { setContextExpanded(true) }) {
                            Icon(
                                imageVector = Icons.TwoTone.MoreVert,
                                contentDescription = stringResource(id = R.string.more_options),
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { setContextExpanded(false) },
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.reply)) },
                                onClick = {
//                                itemUi.id.let { onClickReply(ItemId(it)) }
                                    setContextExpanded(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.TwoTone.Reply,
                                        contentDescription = stringResource(id = R.string.reply),
                                    )
                                },
                            )

                            DropdownMenuItem(text = {
                                Text(
                                    text = stringResource(
                                        id = if (item.upvoted == true) {
                                            R.string.un_vote
                                        } else {
                                            R.string.upvote
                                        },
                                    ),
                                )
                            }, onClick = {
//                                coroutineScope.launch {
//                                    itemUi?.itemUi?.toggleUpvote(onNavigateLogin = onNavigateLogin)
//                                }

                                setContextExpanded(false)
                            }, leadingIcon = {
                                Icon(
                                    imageVector = if (item.upvoted == true) {
                                        Icons.Filled.ThumbUp
                                    } else {
                                        Icons.TwoTone.ThumbUp
                                    },
                                    contentDescription = stringResource(
                                        id = if (item.upvoted == true) {
                                            R.string.un_vote
                                        } else {
                                            R.string.upvote
                                        },
                                    ),
                                )
                            })

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            id = if (item.followed) {
                                                R.string.unfollow
                                            } else {
                                                R.string.follow
                                            },
                                        )
                                    )
                                },
                                onClick = {
//                                coroutineScope.launch {
//                                    itemUi.toggleFollowed()
//                                }

                                    setContextExpanded(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (item.followed) {
                                            Icons.Filled.Quickreply
                                        } else {
                                            Icons.TwoTone.Quickreply
                                        },
                                        contentDescription = stringResource(
                                            id = if (item.followed) {
                                                R.string.unfollow
                                            } else {
                                                R.string.follow
                                            },
                                        ),
                                    )
                                },
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            id = if (item.flagged == true) {
                                                R.string.un_flag
                                            } else {
                                                R.string.flag
                                            },
                                        ),
                                    )
                                },
                                onClick = {
//                                coroutineScope.launch {
//                                    itemUi.toggleFlag(onNavigateLogin)
//                                }

                                    setContextExpanded(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (item.flagged == true) {
                                            Icons.Filled.Flag
                                        } else {
                                            Icons.TwoTone.Flag
                                        },
                                        contentDescription = stringResource(
                                            id = if (item.flagged == true) {
                                                R.string.un_flag
                                            } else {
                                                R.string.flag
                                            },
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }

                val htmlString = if (item.deleted == true) {
                    stringResource(id = R.string.deleted)
                } else {
                    item.text.orEmpty()
                }

                if (item.expanded) {
                    Text(
                        text = rememberAnnotatedHtmlString(htmlString),
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
    }
}

@Composable
fun ThreadDepth(depth: Int, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val threadGap = 8.dp
    Canvas(modifier = modifier.fillMaxHeight().width(threadGap * (depth - 1))) {
        val threadGapPx = 8.dp.toPx()
        val thicknessPx = 1.dp.toPx()
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
