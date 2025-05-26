package com.monoid.hackernews.common.view.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Help
import androidx.compose.material.icons.automirrored.twotone.Login
import androidx.compose.material.icons.automirrored.twotone.Logout
import androidx.compose.material.icons.twotone.Feedback
import androidx.compose.material.icons.twotone.Gavel
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Notifications
import androidx.compose.material.icons.twotone.Policy
import androidx.compose.material.icons.twotone.TextFields
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.about
import com.monoid.hackernews.common.view.appearance
import com.monoid.hackernews.common.view.help
import com.monoid.hackernews.common.view.login
import com.monoid.hackernews.common.view.logout_format
import com.monoid.hackernews.common.view.notifications
import com.monoid.hackernews.common.view.send_feedback
import com.monoid.hackernews.common.view.stories.detailContentInsetSides
import com.monoid.hackernews.common.view.terms_of_service
import com.monoid.hackernews.common.view.user_guidelines
import org.jetbrains.compose.resources.stringResource

@Suppress("ComposeUnstableReceiver")
@Composable
fun SettingsListPane(
    username: Username?,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickAppearance: () -> Unit,
    onClickNotifications: () -> Unit,
    onClickHelp: () -> Unit,
    onClickTermsOfService: () -> Unit,
    onClickUserGuidelines: () -> Unit,
    onClickSendFeedback: () -> Unit,
    onClickAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxHeight(),
        contentPadding = WindowInsets.safeDrawing
            .only(detailContentInsetSides())
            .asPaddingValues(),
    ) {
        item {
            if (username == null) {
                ListItem(
                    headlineContent = { Text(text = stringResource(Res.string.login)) },
                    modifier = Modifier.clickable(onClick = onClickLogin),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.TwoTone.Login,
                            contentDescription = null,
                        )
                    },
                )
            } else {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(Res.string.logout_format, username.string))
                    },
                    modifier = Modifier.clickable(onClick = onClickLogout),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.TwoTone.Logout,
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.appearance))
                },
                modifier = Modifier.clickable(onClick = onClickAppearance),
                leadingContent = {
                    Icon(
                        imageVector = Icons.TwoTone.TextFields,
                        contentDescription = null,
                    )
                },
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.notifications))
                },
                modifier = Modifier.clickable(onClick = onClickNotifications),
                leadingContent = {
                    Icon(
                        imageVector = Icons.TwoTone.Notifications,
                        contentDescription = null,
                    )
                },
            )
        }

        item {
            HorizontalDivider()
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.help))
                },
                modifier = Modifier.clickable(onClick = onClickHelp),
                leadingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.TwoTone.Help,
                        contentDescription = null,
                    )
                },
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.terms_of_service))
                },
                modifier = Modifier.clickable(onClick = onClickTermsOfService),
                leadingContent = {
                    Icon(
                        imageVector = Icons.TwoTone.Policy,
                        contentDescription = null,
                    )
                },
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.user_guidelines))
                },
                modifier = Modifier.clickable(onClick = onClickUserGuidelines),
                leadingContent = {
                    Icon(
                        imageVector = Icons.TwoTone.Gavel,
                        contentDescription = null,
                    )
                },
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.send_feedback))
                },
                modifier = Modifier.clickable(onClick = onClickSendFeedback),
                leadingContent = {
                    Icon(
                        imageVector = Icons.TwoTone.Feedback,
                        contentDescription = null,
                    )
                },
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(Res.string.about))
                },
                modifier = Modifier.clickable(onClick = onClickAbout),
                leadingContent = {
                    Icon(
                        imageVector = Icons.TwoTone.Info,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}
