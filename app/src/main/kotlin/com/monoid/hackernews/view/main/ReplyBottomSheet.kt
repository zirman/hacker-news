package com.monoid.hackernews.view.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.bottomSheet
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.navigation.MainNavigation
import com.monoid.hackernews.view.reply.ReplyContent
import io.ktor.client.HttpClient

fun NavGraphBuilder.replyBottomSheet(
    authentication: DataStore<Authentication>,
    itemTreeRepository: ItemTreeRepository,
    httpClient: HttpClient,
    windowSizeClassState: State<WindowSizeClass>,
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
            httpClient = httpClient,
            itemTreeRepository = itemTreeRepository,
            authentication = authentication,
            itemId = itemId,
            windowSizeClass = windowSizeClassState.value,
            onSuccess = onNavigateUp,
            onError = onLoginError,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        )
    }
}
