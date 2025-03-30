package com.monoid.hackernews.common.view.logout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.cancel
import com.monoid.hackernews.common.view.hacker_news_logout
import com.monoid.hackernews.common.view.logout
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogoutDialogContent(
    onClickLogout: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.hacker_news_logout),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(Res.string.cancel))
                }
                TextButton(onClick = onClickLogout) {
                    Text(text = stringResource(Res.string.logout))
                }
            }
        }
    }
}
