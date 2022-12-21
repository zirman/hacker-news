package com.monoid.hackernews.common.ui.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun rememberUseDarkTheme(): Boolean {
    val configuration: Configuration =
        LocalConfiguration.current

    return remember(configuration.uiMode) {
        configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
    }
}
