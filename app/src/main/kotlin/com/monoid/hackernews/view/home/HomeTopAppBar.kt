package com.monoid.hackernews.view.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.view.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeTopAppBar(
    showItemId: ItemId?,
    drawerState: DrawerState,
    windowSizeClass: WindowSizeClass,
    title: String,
    setSelectedItemId: (ItemId?) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    TopAppBar(
        title = {
            Column {
                Text(text = stringResource(id = R.string.hacker_news))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        },
        navigationIcon = {
            AnimatedVisibility(
                visible = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Expanded ||
                        windowSizeClass.heightSizeClass != WindowHeightSizeClass.Expanded,
            ) {
                val showUpButton: Boolean =
                    remember(showItemId, windowSizeClass.widthSizeClass) {
                        showItemId != null &&
                                windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                    }

                if (showUpButton) {
                    BackHandler { setSelectedItemId(null) }

                    IconButton(onClick = { setSelectedItemId(null) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                        )
                    }
                } else {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.TwoTone.Menu,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            navigationIconContentColor = MaterialTheme.colorScheme.tertiary,
            titleContentColor = MaterialTheme.colorScheme.tertiary,
            actionIconContentColor = MaterialTheme.colorScheme.tertiary,
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}
