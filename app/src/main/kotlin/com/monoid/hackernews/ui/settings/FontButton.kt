package com.monoid.hackernews.ui.settings

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.monoid.hackernews.datastore.copy
import com.monoid.hackernews.settingsDataStore
import com.monoid.hackernews.ui.theme.HNFont
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FontButton(hnFont: HNFont) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    TextButton(
        onClick = {
            coroutineScope.launch {
                context.settingsDataStore.updateData {
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
