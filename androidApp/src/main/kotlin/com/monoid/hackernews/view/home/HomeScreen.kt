@file:OptIn(ExperimentalMaterial3Api::class)

package com.monoid.hackernews.view.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.OrderedItem
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.domain.LiveUpdateUseCase

@Composable
fun HomeScreen(
    itemTreeRepository: ItemTreeRepository,
    drawerState: DrawerState,
    windowSizeClass: WindowSizeClass,
    title: String,
    orderedItemRepo: LiveUpdateUseCase<OrderedItem>,
    snackbarHostState: SnackbarHostState,
    selectedItemId: ItemId?,
    setSelectedItemId: (ItemId?) -> Unit,
    // Used to keep track of if the story was scrolled last.
    detailInteraction: Boolean,
    setDetailInteraction: (Boolean) -> Unit,
    onNavigateToLogin: (LoginAction) -> Unit,
    onClickUser: (Username?) -> Unit,
    onClickReply: (ItemId) -> Unit,
    onClickBrowser: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val showItemId: ItemId?
        by remember(windowSizeClass.widthSizeClass, selectedItemId, detailInteraction) {
            derivedStateOf {
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact ->
                        if (detailInteraction) selectedItemId else null

                    else ->
                        selectedItemId
                }
            }
        }

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())

    Scaffold(
        topBar = {
            val layoutDirection = LocalLayoutDirection.current
            val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()

            HomeTopAppBar(
                showItemId = showItemId,
                drawerState = drawerState,
                windowSizeClass = windowSizeClass,
                title = title,
                setSelectedItemId = setSelectedItemId,
                scrollBehavior = scrollBehavior,
                modifier = Modifier.padding(
                    PaddingValues(
                        start = safeDrawingPadding.calculateStartPadding(layoutDirection),
                        end = safeDrawingPadding.calculateEndPadding(layoutDirection),
                    )
                ),
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeContent.only(WindowInsetsSides.Bottom)
                ),
            )
        },
        content = { paddingValues ->
            HomeContent(
                paddingValues,
                showItemId = showItemId,
                itemTreeRepository = itemTreeRepository,
                windowSizeClass = windowSizeClass,
                orderedItemRepo = orderedItemRepo,
                selectedItemId = selectedItemId,
                setSelectedItemId = setSelectedItemId,
                detailInteraction = detailInteraction,
                setDetailInteraction = setDetailInteraction,
                onNavigateToLogin = onNavigateToLogin,
                onClickUser = onClickUser,
                onClickReply = onClickReply,
                onClickBrowser = onClickBrowser,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    )
}
