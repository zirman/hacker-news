package com.monoid.hackernews.common.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Username
import com.monoid.hackernews.common.domain.navigation.Route.BottomNav
import io.ktor.http.Url
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScaffold(
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickUser: (Username) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickUrl: (Url) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentDestination by rememberSaveable { mutableIntStateOf(0) }
    BackHandler(currentDestination != 0) {
        currentDestination = 0
    }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            BottomNav.entries.forEach { story ->
                item(
                    selected = story == BottomNav.entries[currentDestination],
                    onClick = { currentDestination = story.ordinal },
                    icon = {
                        Icon(
                            imageVector = if (story == BottomNav.entries[currentDestination]) {
                                story.selectedIcon
                            } else {
                                story.icon
                            },
                            contentDescription = stringResource(story.contentDescription),
                        )
                    },
                    label = {
                        Text(text = stringResource(story.label))
                    },
                )
            }
        },
        modifier = modifier.fillMaxSize(),
        content = {
            HomeContent(
                currentDestination = BottomNav.entries[currentDestination],
                onClickLogin = onClickLogin,
                onClickLogout = onClickLogout,
                onClickUser = onClickUser,
                onClickReply = onClickReply,
                onClickUrl = onClickUrl,
            )
        },
    )
}
