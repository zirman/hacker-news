package com.monoid.hackernews.common.view.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.Username
import io.ktor.http.Url

@Composable
expect fun MainNavHost(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickItem: (Item) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    onClickAppearance: () -> Unit,
    onClickNotifications: () -> Unit,
    onClickHelp: () -> Unit,
    onClickTermsOfService: () -> Unit,
    onClickUserGuidelines: () -> Unit,
    onClickSendFeedback: () -> Unit,
    onClickAbout: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
)
