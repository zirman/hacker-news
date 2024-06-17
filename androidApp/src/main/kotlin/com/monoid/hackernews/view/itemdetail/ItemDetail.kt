@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.itemdetail

import android.net.Uri
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.Item
import com.monoid.hackernews.common.data.ItemType
import com.monoid.hackernews.common.ui.util.rememberTimeBy
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.view.TooltipPopupPositionProvider
import com.monoid.hackernews.common.view.placeholder.PlaceholderHighlight
import com.monoid.hackernews.common.view.placeholder.placeholder
import com.monoid.hackernews.common.view.placeholder.shimmer
import com.monoid.hackernews.common.view.rememberAnnotatedHtmlString

@Composable
fun ItemDetail(
    item: Item?,
    onOpenBrowser: (Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading = false // item.lastUpdate == null
    Surface(
        modifier = modifier.placeholder(
            visible = isLoading,
            color = Color.Transparent,
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = LocalContentColor.current.copy(alpha = .5f),
            ),
        ),
        contentColor = MaterialTheme.colorScheme.secondary,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                SelectionContainer(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rememberAnnotatedHtmlString(
                            (if (item?.type == ItemType.Comment) item.text else item?.title) ?: "",
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp),
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
                        ) {
                            if (item.type == ItemType.Story) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(
                                                id = if (
                                                    item.favourited == true
                                                ) {
                                                    R.string.un_favorite
                                                } else {
                                                    R.string.favorite
                                                }
                                            )
                                        )
                                    },
                                    onClick = {
//                                        coroutineScope.launch {
//                                            itemUi.itemUi?.toggleFavorite(onNavigateLogin)
//                                        }
//
//                                        setContextExpanded(false)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (
                                                item.favourited == true
                                            ) {
                                                Icons.Filled.Favorite
                                            } else {
                                                Icons.TwoTone.Favorite
                                            },
                                            contentDescription = stringResource(
                                                id = if (
                                                    item.favourited == true
                                                ) {
                                                    R.string.un_favorite
                                                } else {
                                                    R.string.favorite
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
                                            id = if (item.followed) {
                                                R.string.unfollow
                                            } else {
                                                R.string.follow
                                            }
                                        )
                                    )
                                },
                                onClick = {
//                                    coroutineScope.launch {
//                                        itemUi.itemUi?.toggleFollowed()
//                                    }
//
//                                    setContextExpanded(false)
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
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(
                                            id = if (item.flagged == true) {
                                                R.string.un_flag
                                            } else {
                                                R.string.flag
                                            }
                                        )
                                    )
                                },
                                onClick = {
//                                    coroutineScope.launch {
//                                        itemUi.itemUi?.toggleFlag(onNavigateLogin)
//                                    }
//
//                                    setContextExpanded(false)
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
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val timeUserAnnotatedString: AnnotatedString = rememberTimeBy(
                time = item?.time,
                by = item?.by,
            )

            Text(
                text = timeUserAnnotatedString,
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
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = LocalContentColor.current
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item?.lastUpdate == null || item.type == ItemType.Story) {
                    item?.score.let { score ->
                        key("score") {
                            TooltipBox(
                                positionProvider = TooltipPopupPositionProvider(),
                                tooltip = {
                                    Surface { Text(text = stringResource(id = R.string.upvote)) }
                                },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(
                                    onClick = {
//                                        coroutineScope.launch {
//                                            itemUi?.itemUi?.toggleUpvote(onNavigateLogin)
//                                        }
                                    },
                                    enabled = item?.type == ItemType.Story,
                                ) {
                                    Icon(
                                        imageVector = if (item?.upvoted == true) {
                                            Icons.Filled.ThumbUp
                                        } else {
                                            Icons.TwoTone.ThumbUp
                                        },
                                        contentDescription = stringResource(
                                            id = if (item?.upvoted == true) {
                                                R.string.un_vote
                                            } else {
                                                R.string.upvote
                                            },
                                        ),
                                    )
                                }
                            }

                            Text(
                                text = remember(score) { score?.toString() ?: "" },
                                modifier = Modifier.widthIn(min = 24.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                val descendants = item?.descendants

                key("comments") {
                    TooltipBox(
                        positionProvider = TooltipPopupPositionProvider(),
                        tooltip = {
                            Surface { Text(text = stringResource(id = R.string.comment)) }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = {
//                                item.id.let { onClickReply(ItemId(it)) }
                            },
                            enabled = isLoading.not(),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.Comment,
                                contentDescription = null,
                            )
                        }
                    }

                    Text(
                        text = remember(descendants) { descendants?.toString() ?: "" },
                        maxLines = 1,
                        modifier = Modifier.widthIn(min = 24.dp),
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                    )
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
                            .weight(1f),
//                            .placeholder(
//                                visible = false,
//                                color = Color.Transparent,
//                                shape = MaterialTheme.shapes.small,
//                                highlight = PlaceholderHighlight.shimmer(
//                                    highlightColor = LocalContentColor.current.copy(alpha = .5f)
//                                ),
//                            ),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelLarge,
                    )

                    TooltipBox(
                        positionProvider = TooltipPopupPositionProvider(),
                        tooltip = {
                            Surface {
                                Text(text = stringResource(id = R.string.open_in_browser))
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = {
                                item?.url?.let { onOpenBrowser(item) }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = stringResource(id = R.string.open_in_browser)
                            )
                        }
                    }
                }
            }

            val itemText = item?.text

            if (item?.type != ItemType.Comment && itemText != null) {
                Text(
                    text = rememberAnnotatedHtmlString(itemText),
                    modifier = Modifier
                        .padding(8.dp)
                        .placeholder(
                            visible = false,
                            color = Color.Transparent,
                            shape = MaterialTheme.shapes.small,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = LocalContentColor.current.copy(alpha = .5f),
                            ),
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
