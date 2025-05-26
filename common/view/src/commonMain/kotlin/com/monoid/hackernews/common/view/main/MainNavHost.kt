package com.monoid.hackernews.common.view.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username

@Composable
expect fun MainNavHost(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUser: (Username) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
)
