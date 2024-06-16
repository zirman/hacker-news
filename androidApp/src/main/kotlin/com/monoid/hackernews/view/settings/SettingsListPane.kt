package com.monoid.hackernews.view.settings

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.view.R

@Composable
fun SettingsListPane(
    username: Username?,
    onClickLogin: () -> Unit,
    onClickLogout: () -> Unit,
    onClickStyle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        item {
            if (username == null) {
                Button(onClick = onClickLogin) {
                    Text(text = stringResource(R.string.login))
                }
            } else {
                Text(text = username.string)

                Button(
                    onClick = onClickLogout,
                ) {
                    Text(text = stringResource(R.string.logout))
                }
            }
        }

        item {
            Button(
                onClick = onClickStyle,
            ) {
                Text(text = stringResource(R.string.style))
            }
        }
    }
}
