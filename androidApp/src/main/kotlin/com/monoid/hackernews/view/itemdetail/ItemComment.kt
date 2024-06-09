package com.monoid.hackernews.view.itemdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Reply
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Quickreply
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.ExpandLess
import androidx.compose.material.icons.twotone.ExpandMore
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.SimpleItemUiState
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.ui.text.ClickableTextBlock
import com.monoid.hackernews.common.ui.util.rememberTimeBy
import com.monoid.hackernews.common.view.R

@Composable
fun ItemComment(
    itemUi: SimpleItemUiState,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    onVisible: (ItemId) -> Unit,
    onClick: (ItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        onVisible(itemUi.id)
    }
    Surface(
        modifier = modifier.clickable { onClick(itemUi.id) },
//            .padding(start = (((itemUi?.threadDepth ?: 1) - 1) * 16).dp)
//            .then(if (itemUi == null) Modifier.drawWithContent {} else Modifier),
        shape = MaterialTheme.shapes.medium,
        contentColor = MaterialTheme.colorScheme.secondary,
        tonalElevation = ((itemUi.kids?.size ?: 0) * 10 + 40).dp
    ) {
        Column(
//            modifier = if (itemUi == null) {
//                Modifier
//            } else {
//                Modifier
//                    .clickable {
//                        coroutineScope.launch {
//                            itemUi.itemUi?.toggleExpanded()
//                        }
//                    }
//                    .animateContentSize()
//                    .placeholder(
//                        visible = itemUi.itemUi?.item?.lastUpdate == null,
//                        color = Color.Transparent,
//                        shape = MaterialTheme.shapes.small,
//                        highlight = PlaceholderHighlight.shimmer(
//                            highlightColor = LocalContentColor.current.copy(alpha = .5f),
//                        ),
//                    )
//            },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val timeByUserAnnotatedString: AnnotatedString =
                    rememberTimeBy(time = itemUi.time, by = itemUi.by)

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

                Spacer(modifier = Modifier.weight(1f))

                Box {
                    val (expanded: Boolean, setContextExpanded) =
                        remember { mutableStateOf(false) }

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

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        id = if (itemUi.upvoted == true) R.string.un_vote
                                        else R.string.upvote,
                                    ),
                                )
                            },
                            onClick = {
//                                coroutineScope.launch {
//                                    itemUi?.itemUi?.toggleUpvote(onNavigateLogin = onNavigateLogin)
//                                }

                                setContextExpanded(false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector =
                                    if (itemUi.upvoted == true) Icons.Filled.ThumbUp
                                    else Icons.TwoTone.ThumbUp,
                                    contentDescription = stringResource(
                                        id =
                                        if (itemUi.upvoted == true) R.string.un_vote
                                        else R.string.upvote,
                                    ),
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        id =
                                        if (itemUi.followed == true) R.string.unfollow
                                        else R.string.follow,
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
                                    imageVector =
                                    if (itemUi.followed == true) Icons.Filled.Quickreply
                                    else Icons.TwoTone.Quickreply,
                                    contentDescription = stringResource(
                                        id =
                                        if (itemUi.followed == true) R.string.unfollow
                                        else R.string.follow,
                                    ),
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        id =
                                        if (itemUi.flagged == true) R.string.un_flag
                                        else R.string.flag,
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
                                    imageVector =
                                    if (itemUi.flagged == true) Icons.Filled.Flag
                                    else Icons.TwoTone.Flag,
                                    contentDescription = stringResource(
                                        id = if (itemUi.flagged == true) R.string.un_flag
                                        else R.string.flag,
                                    ),
                                )
                            },
                        )
                    }
                }
            }

            val htmlString = if (itemUi.deleted == true) stringResource(id = R.string.deleted)
            else itemUi.text ?: ""

            val annotatedText = remember(htmlString) { AnnotatedString.fromHtml(htmlString) }

            if (itemUi.expanded == true) {
                SelectionContainer {
                    ClickableTextBlock(
                        text = annotatedText,
                        lines = 2,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        overflow = TextOverflow.Ellipsis,
                        minHeight = true,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                ClickableTextBlock(
                    text = annotatedText,
                    lines = 2,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (itemUi.expanded?.not() == true &&
                (itemUi.kids?.size ?: 0) > 0
            ) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(4.dp),
                ) {
                    Text(
                        text = "${itemUi.kids?.size}",
                        maxLines = 1,
                    )
                }
            } else {
                Icon(
                    imageVector = if (itemUi.expanded == true) Icons.TwoTone.ExpandLess
                    else Icons.TwoTone.ExpandMore,
                    contentDescription = stringResource(id = R.string.expand),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}
