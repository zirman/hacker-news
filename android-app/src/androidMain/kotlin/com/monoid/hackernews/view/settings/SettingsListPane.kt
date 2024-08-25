@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.monoid.hackernews.view.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.login
import com.monoid.hackernews.common.view.logout
import com.monoid.hackernews.common.view.style
import org.jetbrains.compose.resources.stringResource

@Suppress("ComposeUnstableReceiver")
@Composable
fun ThreePaneScaffoldScope.SettingsListPane(
    username: Username?,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickStyle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedPane {
        LazyColumn(
            modifier = modifier
                .preferredWidth(320.dp)
                .fillMaxHeight(),
            contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        ) {
            item {
                if (username == null) {
                    Button(onClick = onClickLogin) {
                        Text(text = stringResource(Res.string.login))
                    }
                } else {
                    Text(text = username.string)

                    Button(onClick = onClickLogout) {
                        Text(text = stringResource(Res.string.logout))
                    }
                }
            }

            item {
                Button(onClick = onClickStyle) {
                    Text(text = stringResource(Res.string.style))
                }
            }
        }
    }
}
