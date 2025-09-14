package com.monoid.hackernews.common.view

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.monoid.hackernews.common.view.login.LoginDialog
import com.monoid.hackernews.common.view.logout.LogoutDialog
import com.monoid.hackernews.common.view.main.MainNavHost
import com.monoid.hackernews.common.view.theme.AppTheme
import io.ktor.http.Url

@Suppress("ComposeModifierMissing")
@Composable
fun App(onClickUrl: (Url) -> Unit) {
    AppTheme {
        Scrim {
            Box(contentAlignment = Alignment.Center) {
                var showLoginDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                var showLogoutDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                MainNavHost(
                    onClickLogin = {
                        showLoginDialog = true
                    },
                    onClickLogout = {
                        showLogoutDialog = true
                    },
                    onClickItem = {},
                    onClickReply = {},
                    onClickUser = {},
                    onClickUrl = onClickUrl,
                    onClickAppearance = {},
                    onClickNotifications = {},
                    onClickHelp = {},
                    onClickTermsOfService = {},
                    onClickUserGuidelines = {},
                    onClickSendFeedback = {},
                    onClickAbout = {},
                )
                if (showLoginDialog) {
                    LoginDialog(
                        onDismissRequest = {
                            showLoginDialog = false
                        },
                    )
                }
                if (showLogoutDialog) {
                    LogoutDialog(
                        onDismissRequest = {
                            showLogoutDialog = false
                        },
                    )
                }
            }
        }
    }
}
