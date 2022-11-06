package com.monoid.hackernews.ui.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.shared.api.ItemId
import com.monoid.hackernews.shared.navigation.MainNavigation
import com.monoid.hackernews.ui.reply.ReplyContent

fun NavGraphBuilder.replyBottomSheet(
    windowSizeClass: WindowSizeClass,
    navigateUp: () -> Unit,
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
            onSuccess = navigateUp,
            onError = onLoginError,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
        )
    }
}
