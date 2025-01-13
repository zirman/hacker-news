@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.common.view.itemlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.ItemType
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.util.rememberTimeBy
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.TooltipPopupPositionProvider
import com.monoid.hackernews.common.view.comment
import com.monoid.hackernews.common.view.favorite
import com.monoid.hackernews.common.view.flag
import com.monoid.hackernews.common.view.follow
import com.monoid.hackernews.common.view.more_options
import com.monoid.hackernews.common.view.open_in_browser
import com.monoid.hackernews.common.view.un_favorite
import com.monoid.hackernews.common.view.un_flag
import com.monoid.hackernews.common.view.un_vote
import com.monoid.hackernews.common.view.unfollow
import com.monoid.hackernews.common.view.upvote
import org.jetbrains.compose.resources.stringResource

@Suppress("CyclomaticComplexMethod")
@Composable
fun Item(
    item: Item,
    onClickItem: (Item) -> Unit,
    onClickReply: (Item) -> Unit,
    onClickUser: (Username) -> Unit,
    onOpenUrl: (Item) -> Unit,
    onClickUpvote: (Item) -> Unit,
    onClickFavorite: (Item) -> Unit,
    onClickFollow: (Item) -> Unit,
    onClickFlag: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isStoryOrComment = item.type == ItemType.Story || item.type == ItemType.Comment
    Surface(
        modifier = modifier.clickable(onClick = { onClickItem(item) }),
        contentColor = LocalContentColor.current,
        tonalElevation = ((item.score ?: 0) / 10).dp
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = item.title ?: item.text ?: AnnotatedString(""),
                    minLines = 2,
                    maxLines = 2,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )

                val (contextExpanded: Int, setContextExpanded) =
                    rememberSaveable { mutableIntStateOf(0) }

                Box {
                    IconButton(
                        onClick = { setContextExpanded(1) },
                        enabled = isStoryOrComment
                    ) {
                        Icon(
                            imageVector = Icons.TwoTone.MoreVert,
                            contentDescription = stringResource(Res.string.more_options),
                        )
                    }

                    DropdownMenu(
                        expanded = contextExpanded != 0,
                        onDismissRequest = { setContextExpanded(0) },
                        modifier = Modifier
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(
                                        if (item.favourited == true) {
                                            Res.string.un_favorite
                                        } else {
                                            Res.string.favorite
                                        },
                                    ),
                                )
                            },
                            onClick = {
                                onClickFavorite(item)
                                setContextExpanded(0)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (item.favourited == true) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.TwoTone.Favorite
                                    },
                                    contentDescription = stringResource(
                                        if (item.favourited == true) {
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
                                onClickFlag(item)
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

            Spacer(modifier = Modifier.height(4.dp))

            val timeUserAnnotatedString: AnnotatedString =
                rememberTimeBy(time = item.time, by = item.by)

            val style = MaterialTheme.typography.labelMedium
                .copy(color = LocalContentColor.current)

            // TODO: add onClickUser handler
            Text(
                text = timeUserAnnotatedString,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(with(LocalDensity.current) { style.lineHeight.toDp() }),
                style = style,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                key("score") {
                    TooltipBox(
                        positionProvider = TooltipPopupPositionProvider(),
                        tooltip = { Surface { Text(stringResource(Res.string.upvote)) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { onClickUpvote(item) },
                            enabled = isStoryOrComment,
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
                    }

                    val score = item.score

                    Text(
                        text = remember(score) { score?.toString().orEmpty() },
                        maxLines = 1,
                        modifier = Modifier.widthIn(min = 24.dp),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                key("comments") {
                    val descendants = item.descendants

                    TooltipBox(
                        positionProvider = TooltipPopupPositionProvider(),
                        tooltip = { Surface { Text(stringResource(Res.string.comment)) } },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { onClickReply(item) },
                            enabled = isStoryOrComment,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.Comment,
                                contentDescription = null,
                            )
                        }
                    }

                    Text(
                        text = remember(descendants) { descendants?.toString().orEmpty() },
                        maxLines = 1,
                        modifier = Modifier.widthIn(min = 24.dp),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                key("url") {
                    if (item.url != null) {
                        val host: String = remember(item.url) {
                            item.url?.let { Url(it) }?.host.orEmpty()
                        }

                        Text(
                            text = host,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelLarge,
                        )

                        TooltipBox(
                            positionProvider = TooltipPopupPositionProvider(),
                            tooltip = { Surface { Text(stringResource(Res.string.open_in_browser)) } },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(onClick = { onOpenUrl(item) }) {
                                Icon(
                                    imageVector = Icons.Filled.OpenInBrowser,
                                    contentDescription = stringResource(Res.string.open_in_browser),
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
