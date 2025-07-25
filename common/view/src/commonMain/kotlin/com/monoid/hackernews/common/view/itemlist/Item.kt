@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.common.view.itemlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import com.monoid.hackernews.common.view.un_favorite
import com.monoid.hackernews.common.view.un_flag
import com.monoid.hackernews.common.view.un_vote
import com.monoid.hackernews.common.view.unfollow
import com.monoid.hackernews.common.view.upvote
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource

@Suppress("CyclomaticComplexMethod")
@Composable
fun Item(
    item: Item,
    onClickItem: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickUpvote: (Item) -> Unit,
    onClickFavorite: (Item) -> Unit,
    onClickFollow: (Item) -> Unit,
    onClickFlag: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isStoryOrComment = item.type == ItemType.Story || item.type == ItemType.Comment
    Surface(
        modifier = modifier.clickable(onClick = { onClickItem(item) }),
        tonalElevation = ((item.score ?: 0) / 10).dp,
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = item.title
                        ?.let { AnnotatedString(it) }
                        ?: item.text
                        ?: AnnotatedString(""),
                    minLines = 2,
                    maxLines = 2,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    style = LocalTextStyle.current.merge(MaterialTheme.typography.titleMedium),
                )
                val (contextExpanded: Boolean, setContextExpanded) =
                    rememberSaveable { mutableStateOf(false) }
                Box {
                    if (isStoryOrComment) {
                        IconButton(onClick = { setContextExpanded(true) }) {
                            Icon(
                                imageVector = Icons.TwoTone.MoreVert,
                                contentDescription = stringResource(Res.string.more_options),
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = contextExpanded,
                        onDismissRequest = { setContextExpanded(false) },
                        modifier = Modifier
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        if (item.favorited == true) {
                                            Res.string.un_favorite
                                        } else {
                                            Res.string.favorite
                                        },
                                    ),
                                )
                            },
                            onClick = {
                                onClickFavorite(item)
                                setContextExpanded(false)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (item.favorited == true) {
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
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        if (item.followed) {
                                            Res.string.unfollow
                                        } else {
                                            Res.string.follow
                                        },
                                    ),
                                )
                            },
                            onClick = {
                                onClickFollow(item)
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
                                onClickFlag(item)
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
            Spacer(modifier = Modifier.height(4.dp))
            val style = LocalTextStyle.current.merge(MaterialTheme.typography.labelMedium)
            // TODO: add onClickUser handler
            Text(
                text = timeBy2(
                    time = item.time,
                    by = item.by,
//                    onClick = onClickUser,
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
                    .height(with(LocalDensity.current) { style.lineHeight.toDp() }),
                style = style,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                key("upvotes") {
                    if (isStoryOrComment) {
                        IconButton(onClick = { onClickUpvote(item) }) {
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
                    }
                    val score = item.score?.toString().orEmpty()
                    Text(
                        text = score,
                        maxLines = 1,
                        modifier = Modifier.widthIn(min = 24.dp),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                key("comments") {
                    val descendants = item.descendants
                    if (isStoryOrComment) {
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
                key("url") {
                    val url = item.url
                    if (url != null) {
                        Text(
                            text = url.host,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Ellipsis,
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
            HorizontalDivider(
                thickness = Dp.Hairline,
                color = LocalContentColor.current,
            )
        }
    }
}
