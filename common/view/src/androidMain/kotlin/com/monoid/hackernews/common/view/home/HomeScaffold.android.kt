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
import androidx.compose.ui.text.style.TextAlign
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.navigation.BottomNav
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun HomeScaffold(
    onClickBrowser: (Item) -> Unit,
    onClickLogin: () -> Unit,
    modifier: Modifier,
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
                        Text(
                            text = stringResource(story.label),
                            textAlign = TextAlign.Center,
                        )
                    },
                )
            }
        },
        modifier = modifier.fillMaxSize(),
        content = {
            HomeContent(
                currentDestination = BottomNav.entries[currentDestination],
                onClickBrowser = onClickBrowser,
                onClickLogin = onClickLogin,
            )
        },
    )
}
