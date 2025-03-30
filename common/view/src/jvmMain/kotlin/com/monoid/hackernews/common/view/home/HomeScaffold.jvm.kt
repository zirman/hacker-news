package com.monoid.hackernews.common.view.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.domain.navigation.BottomNav

@Composable
actual fun HomeScaffold(
    onClickBrowser: (Item) -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateLogout: () -> Unit,
    modifier: Modifier,
) {
    val currentDestination by rememberSaveable { mutableIntStateOf(0) }
    HomeContent(
        currentDestination = BottomNav.entries[currentDestination],
        onClickBrowser = onClickBrowser,
        onNavigateLogin = onNavigateLogin,
        onNavigateLogout = onNavigateLogout,
    )
//    BackHandler(currentDestination != 0) {
//        currentDestination = 0
//    }
//    NavigationSuiteScaffold(
//        navigationSuiteItems = {
//            BottomNav.entries.forEach { story ->
//                item(
//                    selected = story == BottomNav.entries[currentDestination],
//                    onClick = { currentDestination = story.ordinal },
//                    icon = {
//                        Icon(
//                            imageVector = if (story == BottomNav.entries[currentDestination]) {
//                                story.selectedIcon
//                            } else {
//                                story.icon
//                            },
//                            contentDescription = stringResource(story.contentDescription),
//                        )
//                    },
//                    label = {
//                        Text(
//                            text = stringResource(story.label),
//                            textAlign = TextAlign.Center,
//                        )
//                    },
//                )
//            }
//        },
//        modifier = modifier.fillMaxSize(),
//        content = {
//            HomeContent(
//                currentDestination = BottomNav.entries[currentDestination],
//                onClickBrowser = onClickBrowser,
//                onClickLogin = onClickLogin,
//            )
//        },
//    )
}
