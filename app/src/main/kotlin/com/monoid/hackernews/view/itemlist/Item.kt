package com.monoid.hackernews.view.itemlist

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.Comment
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Flag
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material.icons.twotone.Reply
import androidx.compose.material.icons.twotone.ThumbUp
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemUi
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.room.ItemDb
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.ui.text.ClickableTextBlock
import com.monoid.hackernews.view.text.TextBlock
import com.monoid.hackernews.common.ui.util.rememberTimeBy
import com.monoid.hackernews.common.ui.util.userTag
import com.monoid.hackernews.view.util.rememberAnnotatedString

@Preview
@Composable
fun ItemPreview() {
    Item(
        itemUi = object : ItemUi() {
            override val item: ItemDb = ItemDb(
                id = 0,
                type = "story",
                title = "Hello World",
                text = "Lorum Ipsum",
                url = "https://www.google.com/"
            )
            override val kids: List<ItemId> = emptyList()
            override val isUpvote: Boolean = false
            override val isFavorite: Boolean = false
            override val isFlag: Boolean = false
            override val isExpanded: Boolean = false
            override suspend fun toggleUpvote(onNavigateLogin: (LoginAction) -> Unit) {}
            override suspend fun toggleFavorite(onNavigateLogin: (LoginAction) -> Unit) {}
            override suspend fun toggleFlag(onNavigateLogin: (LoginAction) -> Unit) {}
            override suspend fun toggleExpanded() {}
        },
        isSelected = false,
        onClickDetail = {},
        onClickReply = {},
        onClickUser = {},
        onClickBrowser = {},
        onClickUpvote = {},
        onClickFavorite = {},
        onClickFlag = {}
    )
}

@Composable
fun Item(
    itemUi: ItemUi?,
    isSelected: Boolean,
    onClickDetail: () -> Unit,
    onClickReply: () -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickBrowser: () -> Unit,
    onClickUpvote: () -> Unit,
    onClickFavorite: () -> Unit,
    onClickFlag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = itemUi?.item
    val isLoading = item == null

    val placeholderModifier = Modifier
        .placeholder(
            visible = isLoading,
            color = Color.Transparent,
            shape = MaterialTheme.shapes.small,
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = LocalContentColor.current.copy(alpha = .5f),
            )
        )

    val isStoryOrComment = (item?.type == "story" || item?.type == "comment")

    val notStoryAndCommentModifier =
        if (isStoryOrComment.not()) {
            Modifier.drawWithContent { }
        } else {
            Modifier
        }

    Surface(
        modifier = modifier.clickable { onClickBrowser() },
        contentColor = LocalContentColor.current,
        tonalElevation = ((item?.score ?: 0) / 10).dp
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                SelectionContainer(modifier = Modifier.weight(1f)) {
                    TextBlock(
                        text = rememberAnnotatedString(
                            text = item?.title ?: item?.text ?: "",
                            linkColor = LocalContentColor.current
                        ),
                        lines = 2,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .then(placeholderModifier),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                val (contextExpanded: Boolean, setContextExpanded) =
                    rememberSaveable { mutableStateOf(false) }

                Box {
                    IconButton(
                        onClick = { setContextExpanded(true) },
                        modifier = Modifier
                            .then(placeholderModifier)
                            .then(notStoryAndCommentModifier),
                        enabled = isStoryOrComment
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.MoreVert,
                            contentDescription = stringResource(id = R.string.more_options)
                        )
                    }

                    DropdownMenu(
                        expanded = contextExpanded,
                        onDismissRequest = { setContextExpanded(false) },
                        modifier = Modifier
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.reply)) },
                            onClick = {
                                onClickReply()
                                setContextExpanded(false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.TwoTone.Reply,
                                    contentDescription = stringResource(id = R.string.reply)
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        id = if (itemUi?.isFavorite == true) {
                                            R.string.un_favorite
                                        } else {
                                            R.string.favorite
                                        }
                                    )
                                )
                            },
                            onClick = {
                                onClickFavorite()

                                setContextExpanded(false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (itemUi?.isFavorite == true) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.TwoTone.Favorite
                                    },
                                    contentDescription = stringResource(
                                        id = if (itemUi?.isFavorite == true) {
                                            R.string.un_favorite
                                        } else {
                                            R.string.favorite
                                        }
                                    )
                                )
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        id = if (itemUi?.isFlag == true) {
                                            R.string.un_flag
                                        } else {
                                            R.string.flag
                                        }
                                    )
                                )
                            },
                            onClick = {
                                onClickFlag()
                                setContextExpanded(false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (itemUi?.isFlag == true) {
                                        Icons.Filled.Flag
                                    } else {
                                        Icons.TwoTone.Flag
                                    },
                                    contentDescription = stringResource(
                                        id = if (itemUi?.isFlag == true) {
                                            R.string.un_flag
                                        } else {
                                            R.string.flag
                                        }
                                    )
                                )
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val timeUserAnnotatedString: State<AnnotatedString> =
                rememberUpdatedState(
                    item
                        ?.let { rememberTimeBy(it) }
                        ?: AnnotatedString("")
                )

            ClickableTextBlock(
                text = timeUserAnnotatedString.value,
                lines = 1,
                onClick = { offset ->
                    val username = timeUserAnnotatedString.value
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
                        onClickBrowser()
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .then(placeholderModifier),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = LocalContentColor.current
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val score = item?.score

                key("score") {
                    IconButton(
                        onClick = onClickUpvote,
                        modifier = Modifier
                            .then(placeholderModifier)
                            .then(notStoryAndCommentModifier),
                        enabled = isStoryOrComment
                    ) {
                        Icon(
                            imageVector = if (itemUi?.isUpvote == true) {
                                Icons.Filled.ThumbUp
                            } else {
                                Icons.TwoTone.ThumbUp
                            },
                            contentDescription = stringResource(
                                id = if (itemUi?.isUpvote == true) {
                                    R.string.un_vote
                                } else {
                                    R.string.upvote
                                }
                            )
                        )
                    }

                    TextBlock(
                        text = remember(score) { score?.toString() ?: "" },
                        lines = 1,
                        modifier = Modifier
                            .widthIn(min = 24.dp)
                            .then(placeholderModifier),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                val descendants = item?.descendants

                key("comments") {
                    IconButton(
                        onClick = onClickDetail,
                        modifier = placeholderModifier,
                        enabled = item != null
                    ) {
                        Icon(
                            imageVector = if (isSelected) {
                                Icons.Filled.Comment
                            } else {
                                Icons.TwoTone.Comment
                            },
                            contentDescription = null
                        )
                    }

                    TextBlock(
                        text = remember(descendants) { descendants?.toString() ?: "" },
                        lines = 1,
                        modifier = Modifier
                            .widthIn(min = 24.dp)
                            .then(placeholderModifier),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                val host: String =
                    remember(itemUi?.item?.url) {
                        itemUi?.item?.url?.let { Uri.parse(it) }?.host ?: ""
                    }

                TextBlock(
                    text = host,
                    lines = 1,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                        .then(placeholderModifier),
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Divider(
                color = LocalContentColor.current,
                thickness = Dp.Hairline
            )
        }
    }
}
