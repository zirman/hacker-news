package com.monoid.hackernews.view.settings

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.datastore.copy
import com.monoid.hackernews.view.theme.HNFont
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FontButton(
    hnFont: HNFont,
    authentication: DataStore<Authentication>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    TextButton(
        onClick = {
            coroutineScope.launch {
                authentication.updateData {
                    it.copy {
                        font = Json.encodeToString(hnFont)
                    }
                }
            }
        },
        modifier = modifier,
    ) {
        Text(
            text = hnFont.getName(),
            fontFamily = hnFont.rememberFontFamily(),
        )
    }
}
