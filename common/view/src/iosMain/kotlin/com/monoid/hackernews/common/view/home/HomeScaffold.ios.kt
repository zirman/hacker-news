package com.monoid.hackernews.common.view.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.domain.navigation.BottomNav

@Composable
actual fun HomeScaffold(
    onClickBrowser: (Item) -> Unit,
    onNavigateLogin: () -> Unit,
    modifier: Modifier
) {
    HomeContent(
        currentDestination = BottomNav.Stories,
        onClickBrowser = onClickBrowser,
        onNavigateLogin = onNavigateLogin,
    )
}
