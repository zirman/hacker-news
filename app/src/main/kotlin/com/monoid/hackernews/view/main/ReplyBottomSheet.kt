package com.monoid.hackernews.view.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.view.reply.ReplyContent

fun NavGraphBuilder.replyBottomSheet(
    windowSizeClass: WindowSizeClass,
    onNavigateUp: () -> Unit,
    onLoginError: (Throwable) -> Unit,
) {
    bottomSheet(
        route = MainNavigation.Reply.route,
        arguments = MainNavigation.Reply.arguments,
    ) { navBackStackEntry ->
        val itemId: ItemId =
            MainNavigation.Reply.argsFromRoute(navBackStackEntry = navBackStackEntry)

        ReplyContent(
            itemId = itemId,
            windowSizeClass = windowSizeClass,
            onSuccess = onNavigateUp,
            onError = onLoginError,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
        )
    }
}
