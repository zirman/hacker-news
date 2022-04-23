package com.monoid.hackernews.ui.itemdetail

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.ExpandLess
import androidx.compose.material.icons.twotone.ExpandMore
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material.icons.twotone.Reply
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
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.R
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.onClick
import com.monoid.hackernews.rememberAnnotatedString
import com.monoid.hackernews.room.ItemRow
import com.monoid.hackernews.ui.text.ClickableTextBlock
import com.monoid.hackernews.ui.text.TextBlock
import com.monoid.hackernews.ui.util.rememberTimeBy
import com.monoid.hackernews.ui.util.userTag
import androidx.compose.material.MaterialTheme as MaterialTheme2

@Composable
fun CommentItem(
    commentItem: ItemRow,
    isUpvote: Boolean,
    loadingBrush: Brush?,
    setExpanded: (Boolean) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUpvote: (ItemId) -> Unit,
    onClickUnUpvote: (ItemId) -> Unit,
    onClickReply: (ItemId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.padding(start = ((commentItem.depth - 1) * 16).dp),
        shape = MaterialTheme2.shapes.medium,
        contentColor = MaterialTheme.colorScheme.secondary,
        tonalElevation = (commentItem.kidCount * 10 + 40).dp,
    ) {
        val expandedState: State<Boolean> =
            rememberUpdatedState(commentItem.expanded)

        Column(
            modifier = Modifier
                .clickable { setExpanded(expandedState.value.not()) }
                .let { if (loadingBrush != null) it.background(loadingBrush) else it }
                .animateContentSize(),
        ) {
            val isDeleted = commentItem.item.text == null && commentItem.item.lastUpdate != null

            Row(verticalAlignment = Alignment.CenterVertically) {
                val timeByUserAnnotatedString: AnnotatedString =
                    rememberTimeBy(commentItem.item)

                ClickableTextBlock(
                    text = timeByUserAnnotatedString,
                    lines = 1,
                    onClick = { offset ->
                        if (expandedState.value.not()) {
                            setExpanded(true)
                        } else {
                            val username: Username? = timeByUserAnnotatedString
                                .getStringAnnotations(
                                    tag = userTag,
                                    start = offset,
                                    end = offset,
                                )
                                .firstOrNull()
                                ?.item
                                ?.let { Username(it) }

                            if (username != null) {
                                onClickUser(username)
                            } else {
                                setExpanded(false)
                            }
                        }
                    },
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

                    IconButton(
                        onClick = { setContextExpanded(true) },
                        enabled = loadingBrush == null,
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.MoreVert,
                            contentDescription = stringResource(id = R.string.more_options),
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { setContextExpanded(false) },
                    ) {
                        val itemIdState: State<ItemId> =
                            rememberUpdatedState(ItemId(commentItem.item.id))

                        val isUpvoteState: State<Boolean> =
                            rememberUpdatedState(isUpvote)

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        id = if (isUpvoteState.value) {
                                            R.string.un_vote
                                        } else {
                                            R.string.upvote
                                        }
                                    ),
                                )
                            },
                            onClick = {
                                setContextExpanded(false)

                                if (isUpvoteState.value) {
                                    onClickUnUpvote(itemIdState.value)
                                } else {
                                    onClickUpvote(itemIdState.value)
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isUpvoteState.value) {
                                        Icons.Filled.ThumbUp
                                    } else {
                                        Icons.TwoTone.ThumbUp
                                    },
                                    contentDescription = stringResource(
                                        id = if (isUpvoteState.value) {
                                            R.string.un_vote
                                        } else {
                                            R.string.upvote
                                        }
                                    ),
                                )
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.reply)) },
                            onClick = {
                                setContextExpanded(false)
                                onClickReply(itemIdState.value)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.TwoTone.Reply,
                                    contentDescription = stringResource(id = R.string.reply),
                                )
                            },
                        )
                    }
                }
            }

            val text =
                if (isDeleted) {
                    stringResource(id = R.string.deleted)
                } else {
                    commentItem.item.text ?: ""
                }

            val annotatedText: AnnotatedString =
                rememberAnnotatedString(text = text)

            val annotatedTextState: State<AnnotatedString> =
                rememberUpdatedState(annotatedText)

            // context must be wrapped or onClick handler is not called
            val contextState: State<Context> =
                rememberUpdatedState(LocalContext.current)

            ClickableTextBlock(
                text = annotatedText,
                lines = 2,
                onClick = { offset ->
                    if (expandedState.value.not() ||
                        annotatedTextState.value.onClick(contextState.value, offset = offset).not()
                    ) {
                        setExpanded(expandedState.value.not())
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                overflow = TextOverflow.Ellipsis,
                minHeight = commentItem.expanded,
                style = MaterialTheme.typography.bodyMedium,
            )

            if (commentItem.expanded.not() && commentItem.kidCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(4.dp),
                ) {
                    TextBlock(
                        text = "${commentItem.kidCount}",
                        lines = 1,
                    )
                }
            } else {
                Icon(
                    imageVector = if (commentItem.expanded) Icons.TwoTone.ExpandLess
                    else Icons.TwoTone.ExpandMore,
                    contentDescription = stringResource(id = R.string.expand),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}
