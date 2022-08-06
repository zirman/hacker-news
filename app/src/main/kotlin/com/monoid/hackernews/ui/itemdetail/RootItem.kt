package com.monoid.hackernews.ui.itemdetail

import android.content.Context
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Flag
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material.icons.twotone.Reply
import androidx.compose.material.icons.twotone.ThumbUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.monoid.hackernews.R
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.data.ItemUiWithThreadDepth
import com.monoid.hackernews.navigation.LoginAction
import com.monoid.hackernews.ui.text.TextBlock
import com.monoid.hackernews.ui.util.rememberTimeBy
import com.monoid.hackernews.ui.util.userTag
import com.monoid.hackernews.util.onClick
import com.monoid.hackernews.util.rememberAnnotatedString

@Composable
fun RootItem(
    itemUiState: State<ItemUiWithThreadDepth?>,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickBrowser: (String) -> Unit,
    onNavigateLogin: (LoginAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val item = itemUiState.value?.itemUi?.item

    Surface(
        modifier = modifier.then(
            if (item?.url == null) {
                Modifier
            } else {
                Modifier.clickable { onClickBrowser(item.url) }
            }
        ),
        contentColor = MaterialTheme.colorScheme.secondary,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                SelectionContainer(modifier = Modifier.weight(1f)) {
                    TextBlock(
                        text = rememberAnnotatedString(
                            text = (if (item?.type == "comment") item.text else item?.title) ?: "",
                            linkColor = LocalContentColor.current,
                        ),
                        lines = 2,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .placeholder(
                                visible = itemUiState.value == null,
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.small,
                                highlight = PlaceholderHighlight.shimmer(
                                    highlightColor = LocalContentColor.current.copy(alpha = .5f),
                                ),
                            ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                val (contextExpanded: Boolean, setContextExpanded) =
                    rememberSaveable { mutableStateOf(false) }

                if (item?.lastUpdate != null) {
                    Box {
                        IconButton(onClick = { setContextExpanded(true) }) {
                            Icon(
                                imageVector = Icons.TwoTone.MoreVert,
                                contentDescription = stringResource(id = R.string.more_options),
                            )
                        }

                        DropdownMenu(
                            expanded = contextExpanded,
                            onDismissRequest = { setContextExpanded(false) },
                            modifier = Modifier,
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.reply)) },
                                onClick = {
                                    onClickReply(ItemId(item.id))
                                    setContextExpanded(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.TwoTone.Reply,
                                        contentDescription = stringResource(id = R.string.reply),
                                    )
                                },
                            )

                            if (item.type == "story") {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(
                                                id = if (
                                                    itemUiState.value?.itemUi?.isFavorite == true
                                                ) {
                                                    R.string.un_favorite
                                                } else {
                                                    R.string.favorite
                                                },
                                            )
                                        )
                                    },
                                    onClick = {
                                        itemUiState.value?.itemUi?.toggleFavorite(onNavigateLogin)
                                        setContextExpanded(false)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (
                                                itemUiState.value?.itemUi?.isFavorite == true
                                            ) {
                                                Icons.Filled.Favorite
                                            } else {
                                                Icons.TwoTone.Favorite
                                            },
                                            contentDescription = stringResource(
                                                id = if (
                                                    itemUiState.value?.itemUi?.isFavorite == true
                                                ) {
                                                    R.string.un_favorite
                                                } else {
                                                    R.string.favorite
                                                },
                                            ),
                                        )
                                    },
                                )
                            }

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            id = if (
                                                itemUiState.value?.itemUi?.isFlag == true
                                            ) {
                                                R.string.un_flag
                                            } else {
                                                R.string.flag
                                            },
                                        )
                                    )
                                },
                                onClick = {
                                    itemUiState.value?.itemUi?.toggleFlag(onNavigateLogin)
                                    setContextExpanded(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (
                                            itemUiState.value?.itemUi?.isFlag == true
                                        ) {
                                            Icons.Filled.Flag
                                        } else {
                                            Icons.TwoTone.Flag
                                        },
                                        contentDescription = stringResource(
                                            id = if (
                                                itemUiState.value?.itemUi?.isFlag == true
                                            ) {
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
            }

            Spacer(modifier = Modifier.height(4.dp))

            val timeUserAnnotatedString: State<AnnotatedString> =
                rememberUpdatedState(rememberTimeBy(item))

            ClickableText(
                text = timeUserAnnotatedString.value,
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
                    } else if (item?.url != null) {
                        onClickBrowser(item.url)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .placeholder(
                        visible = itemUiState.value == null,
                        color = Color.Transparent,
                        shape = MaterialTheme.shapes.small,
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = LocalContentColor.current.copy(alpha = .5f),
                        ),
                    ),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = LocalContentColor.current,
                ),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item?.lastUpdate == null || item.type == "story") {
                    item?.score.let { score ->
                        key("score") {
                            IconButton(
                                onClick = { itemUiState.value?.itemUi?.toggleUpvote(onNavigateLogin) },
                                enabled = item?.type == "story",
                            ) {
                                Icon(
                                    imageVector = if (itemUiState.value?.itemUi?.isUpvote == true) {
                                        Icons.Filled.ThumbUp
                                    } else {
                                        Icons.TwoTone.ThumbUp
                                    },
                                    contentDescription = stringResource(
                                        id = if (itemUiState.value?.itemUi?.isUpvote == true) {
                                            R.string.un_vote
                                        } else {
                                            R.string.upvote
                                        },
                                    ),
                                )
                            }

                            Text(
                                text = remember(score) { score?.toString() ?: "" },
                                modifier = Modifier
                                    .widthIn(min = 24.dp)
                                    .placeholder(
                                        visible = itemUiState.value == null,
                                        color = Color.Transparent,
                                        shape = MaterialTheme.shapes.small,
                                        highlight = PlaceholderHighlight.shimmer(
                                            highlightColor = LocalContentColor.current
                                                .copy(alpha = .5f),
                                        ),
                                    ),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }

                val host: String? =
                    remember(item, item?.url) {
                        if (item?.lastUpdate != null) {
                            item.url?.let { Uri.parse(it) }?.host
                        } else {
                            ""
                        }
                    }

                if (host != null) {
                    Text(
                        text = host,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                            .placeholder(
                                visible = itemUiState.value == null,
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.small,
                                highlight = PlaceholderHighlight.shimmer(
                                    highlightColor = LocalContentColor.current.copy(alpha = .5f),
                                ),
                            ),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            if (item?.type != "comment" && item?.text != null) {
                val annotatedText: AnnotatedString =
                    rememberAnnotatedString(text = item.text)

                // state wrapper must be used in callbacks or onClicks may not be handled
                val annotatedTextState: State<AnnotatedString> =
                    rememberUpdatedState(annotatedText)

                val contextState: State<Context> =
                    rememberUpdatedState(LocalContext.current)

                ClickableText(
                    text = annotatedText,
                    modifier = Modifier
                        .padding(8.dp)
                        .placeholder(
                            visible = itemUiState.value == null,
                            color = Color.Transparent,
                            shape = MaterialTheme.shapes.small,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = LocalContentColor.current.copy(alpha = .5f),
                            ),
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = { offset ->
                        annotatedTextState.value.onClick(
                            context = contextState.value,
                            offset = offset,
                        )
                    },
                )
            }
        }
    }
}
