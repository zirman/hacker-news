package com.monoid.hackernews.common.view.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.domain.navigation.BottomNav

@Composable
actual fun HomeScaffold(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier
) {
    HomeContent(
        currentDestination = BottomNav.Stories,
        onClickLogin = onClickLogin,
        onClickLogout = onClickLogout,
        onClickUrl = onClickUrl,
    )
}
