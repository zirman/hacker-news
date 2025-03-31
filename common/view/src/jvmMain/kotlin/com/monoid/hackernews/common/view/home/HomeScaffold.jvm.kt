package com.monoid.hackernews.common.view.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.Url
import com.monoid.hackernews.common.domain.navigation.BottomNav

@Composable
actual fun HomeScaffold(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier,
) {
    val currentDestination by rememberSaveable { mutableIntStateOf(0) }
    HomeContent(
        currentDestination = BottomNav.entries[currentDestination],
        onClickLogin = onClickLogin,
        onClickLogout = onClickLogout,
        onClickUrl = onClickUrl,
    )
}
