package com.monoid.hackernews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.Route.BottomNav
import io.ktor.http.Url

@Composable
fun HomeScaffold(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDestination by rememberSaveable { mutableIntStateOf(0) }
    HomeContent(
        currentDestination = BottomNav.entries[currentDestination],
        onClickLogin = onClickLogin,
        onClickLogout = onClickLogout,
        onClickUser = onClickUser,
        onClickReply = onClickReply,
        onClickUrl = onClickUrl,
        modifier = modifier,
    )
}
