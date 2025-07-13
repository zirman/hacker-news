@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.common.view.itemdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Quickreply
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Flag
import androidx.compose.material.icons.twotone.MoreVert
import androidx.compose.material.icons.twotone.Quickreply
import androidx.compose.material.icons.twotone.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.util.timeBy2
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.favorite
import com.monoid.hackernews.common.view.flag
import com.monoid.hackernews.common.view.follow
import com.monoid.hackernews.common.view.more_options
import com.monoid.hackernews.common.view.placeholder.PlaceholderHighlight
import com.monoid.hackernews.common.view.placeholder.placeholder
import com.monoid.hackernews.common.view.placeholder.shimmer
import com.monoid.hackernews.common.view.un_favorite
import com.monoid.hackernews.common.view.un_flag
import com.monoid.hackernews.common.view.un_vote
import com.monoid.hackernews.common.view.unfollow
import com.monoid.hackernews.common.view.upvote
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource

@Suppress("CyclomaticComplexMethod")
@Composable
fun ItemDetail(
    item: Item?,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUpvote: (Item) -> Unit,
    onClickFavorite: (Item) -> Unit,
    onClickFollow: (Item) -> Unit,
    onClickFlag: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 4.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (item?.type == ItemType.Comment) {
                        item.text ?: AnnotatedString("")
                    } else {
                        AnnotatedString(item?.title.orEmpty())
                    },
                    modifier = Modifier.weight(1f),
                    style = LocalTextStyle.current.merge(MaterialTheme.typography.titleMedium),
                )
                val (contextExpanded: Boolean, setContextExpanded) =
                    rememberSaveable { mutableStateOf(false) }
                if (item?.lastUpdate != null) {
                    Box {
                        IconButton(onClick = { setContextExpanded(true) }) {
                            Icon(
                                imageVector = Icons.TwoTone.MoreVert,
                                contentDescription = stringResource(Res.string.more_options),
                            )
                        }
                        DropdownMenu(
                            expanded = contextExpanded,
                            onDismissRequest = { setContextExpanded(false) },
                        ) {
                            if (item.type == ItemType.Story) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(
                                                if (item.favorited == true) {
                                                    Res.string.un_favorite
                                                } else {
                                                    Res.string.favorite
                                                }
                                            )
                                        )
                                    },
                                    onClick = { onClickFavorite(item) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (
                                                item.favorited == true
                                            ) {
                                                Icons.Filled.Favorite
                                            } else {
                                                Icons.TwoTone.Favorite
                                            },
                                            contentDescription = stringResource(
                                                if (item.favorited == true) {
                                                    Res.string.un_favorite
                                                } else {
                                                    Res.string.favorite
                                                },
                                            ),
                                        )
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            if (item.followed) {
                                                Res.string.unfollow
                                            } else {
                                                Res.string.follow
                                            }
                                        )
                                    )
                                },
                                onClick = { onClickFollow(item) },
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
                                }
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
                                onClick = { onClickFlag(item) },
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
            }
            val itemText = item?.text
            if (item?.type != ItemType.Comment && itemText != null) {
                Text(
                    text = itemText,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .placeholder(
                            visible = false,
                            color = Color.Transparent,
                            shape = MaterialTheme.shapes.small,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = LocalContentColor.current.copy(alpha = .5f),
                            ),
                        ),
                    style = htmlTextStyle().merge(MaterialTheme.typography.bodyMedium),
                )
            }
            Text(
                text = timeBy2(
                    time = item?.time,
                    by = item?.by,
//                    onClick = onClickUser,
                ),
//                onClick = { offset ->
//                    val username = timeUserAnnotatedString.value
//                        .getStringAnnotations(
//                            tag = userTag,
//                            start = offset,
//                            end = offset
//                        )
//                        .firstOrNull()
//                        ?.item
//                        ?.let { Username(it) }
//
//                    if (username != null) {
//                        onClickUser(username)
//                    }
//                },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                style = LocalTextStyle.current.merge(MaterialTheme.typography.labelMedium),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item != null && (item.lastUpdate == null || item.type == ItemType.Story)) {
                    item.score.let { score ->
                        key("score") {
                            IconButton(
                                onClick = { onClickUpvote(item) },
                                enabled = item.type == ItemType.Story,
                            ) {
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
                            }
                            Text(
                                text = score?.toString().orEmpty(),
                                modifier = Modifier.widthIn(min = 24.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                val descendants = item?.descendants
                key("comments") {
                    if (item != null && (item.type == ItemType.Story || item.type == ItemType.Comment)) {
                        IconButton(onClick = { onClickReply(item.id) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.Comment,
                                contentDescription = null,
                            )
                        }
                    }
                    Text(
                        text = descendants?.toString().orEmpty(),
                        maxLines = 1,
                        modifier = Modifier.widthIn(min = 24.dp),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                val url = item?.takeIf { it.lastUpdate != null }?.url
                if (item != null && url != null) {
                    Text(
                        text = url.host,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    IconButton(onClick = { onClickUrl(url) }) {
                        var showFavicon by remember { mutableStateOf(true) }
                        if (showFavicon) {
                            AsyncImage(
                                model = item.favicon.toString(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                onError = { showFavicon = false },
                                modifier = Modifier.size(24.dp),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}
