package com.monoid.hackernews.view.settings

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.core.DataStore
import com.monoid.hackernews.shared.datastore.Authentication
import com.monoid.hackernews.shared.datastore.copy
import com.monoid.hackernews.view.theme.HNFont
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FontButton(
    hnFont: HNFont,
    authentication: DataStore<Authentication>,
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
    ) {
        Text(
            text = hnFont.getName(),
            fontFamily = hnFont.rememberFontFamily(),
        )
    }
}
