package com.monoid.hackernews.ui.itemdetail

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.FavoriteBorder
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material.icons.twotone.OpenInBrowser
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.R
import com.monoid.hackernews.Username
import com.monoid.hackernews.api.ItemId
import com.monoid.hackernews.onClick
import com.monoid.hackernews.rememberAnnotatedString
import com.monoid.hackernews.room.ItemDb
import com.monoid.hackernews.ui.util.rememberTimeBy
import com.monoid.hackernews.ui.util.userTag

@Composable
fun RootItem(
    item: ItemDb,
    isUpvote: Boolean,
    isFavorite: Boolean,
    loadingBrush: Brush?,
    onClickUpvote: (ItemId) -> Unit,
    onClickUnUpvote: (ItemId) -> Unit,
    onClickFavorite: (ItemId) -> Unit,
    onClickUnFavorite: (ItemId) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickBrowser: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.secondary,
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = rememberAnnotatedString(
                        text = (if (item.type == "comment") item.text else item.title) ?: "",
                        linkColor = LocalContentColor.current,
                    ),
                    modifier = Modifier.weight(1f)
                        .padding(start = 8.dp)
                        .let { if (loadingBrush != null) it.background(loadingBrush) else it },
                    style = MaterialTheme.typography.titleMedium,
                )

                val (contextExpanded: Boolean, setContextExpanded) =
                    rememberSaveable { mutableStateOf(false) }

                if (item.lastUpdate != null) {
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
                            if (item.type == "story") {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(
                                                id = if (isFavorite) {
                                                    R.string.un_favorite
                                                } else {
                                                    R.string.favorite
                                                },
                                            )
                                        )
                                    },
                                    onClick = {
                                        setContextExpanded(false)

                                        if (isFavorite) {
                                            onClickUnFavorite(ItemId(item.id))
                                        } else {
                                            onClickFavorite(ItemId(item.id))
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (isFavorite) {
                                                Icons.Filled.Favorite
                                            } else {
                                                Icons.TwoTone.FavoriteBorder
                                            },
                                            contentDescription = stringResource(
                                                id = if (isFavorite) {
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
                                text = { Text(text = stringResource(id = R.string.reply)) },
                                onClick = {
                                    setContextExpanded(false)
                                    onClickReply(ItemId(item.id))
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
            }

            Spacer(modifier = Modifier.height(4.dp))

            val timeUserAnnotatedString: AnnotatedString =
                rememberTimeBy(item)

            ClickableText(
                text = timeUserAnnotatedString,
                onClick = { offset ->
                    val username = timeUserAnnotatedString
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
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .let { if (loadingBrush != null) it.background(loadingBrush) else it },
                style = MaterialTheme.typography.labelMedium.copy(
                    color = LocalContentColor.current,
                ),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.lastUpdate == null || item.type == "story") {
                    item.score.let { score ->
                        key("score") {
                            IconButton(
                                onClick = {
                                    if (isUpvote) {
                                        onClickUnUpvote(ItemId(item.id))
                                    } else {
                                        onClickUpvote(ItemId(item.id))
                                    }
                                },
                                enabled = item.type == "story",
                            ) {
                                Icon(
                                    imageVector = if (isUpvote) {
                                        Icons.Filled.ThumbUp
                                    } else {
                                        Icons.TwoTone.ThumbUp
                                    },
                                    contentDescription = stringResource(
                                        id = if (isUpvote) {
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
                                    .let {
                                        if (loadingBrush != null) {
                                            it.background(loadingBrush)
                                        } else {
                                            it
                                        }
                                    },
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }

                val host: String? =
                    remember(item, item.url) {
                        if (item.lastUpdate != null) {
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
                            .let {
                                if (loadingBrush != null) {
                                    it.background(loadingBrush)
                                } else {
                                    it
                                }
                            },
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelLarge,
                    )

                    IconButton(
                        onClick = {
                            if (item.url != null) {
                                onClickBrowser(item.url)
                            }
                        },
                        enabled = item.url != null,
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.OpenInBrowser,
                            contentDescription = null,
                        )
                    }
                }
            }

            if (item.type != "comment" && item.text != null) {
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
                        .let { if (loadingBrush != null) it.background(loadingBrush) else it },
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
