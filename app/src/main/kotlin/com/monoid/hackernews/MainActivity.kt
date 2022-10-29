package com.monoid.hackernews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.monoid.hackernews.shared.settingsDataStore
import com.monoid.hackernews.ui.main.MainContent
import com.monoid.hackernews.ui.theme.AppTheme
import com.monoid.hackernews.ui.theme.HNFont
import com.monoid.hackernews.shared.ui.util.rememberUseDarkTheme
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val useDarkTheme: Boolean =
                rememberUseDarkTheme()

            val fontState: State<String?> = remember { settingsDataStore.data.map { it.font } }
                .collectAsState(null)

            AppTheme(
                useDarkTheme = useDarkTheme,
                hnFont = remember(fontState.value) {
                    fontState.value
                        ?.let { font ->
                            try {
                                Json.decodeFromString<HNFont>(font)
                            } catch (error: Throwable) {
                                null
                            }
                        }
                        ?: HNFont.Default
                },
            ) {
                val systemUiController: SystemUiController =
                    rememberSystemUiController()

                val primaryColor = MaterialTheme.colorScheme.primary

                SideEffect {
                    systemUiController.setStatusBarColor(
                        // Opaque color is used for window bar on Chrome OS.
                        color = primaryColor.copy(alpha = 0f),
                        darkIcons = useDarkTheme.not(),
                    )

                    systemUiController.setNavigationBarColor(
                        // if alpha is zero, color is ignored
                        color = Color(red = 128, green = 128, blue = 128, alpha = 1),
                        darkIcons = useDarkTheme.not(),
                    )
                }

                MainContent(
                    windowSizeClass = calculateWindowSizeClass(LocalContext.current as Activity)
                )
            }

            LaunchedEffect(Unit) {
                reportFullyDrawn()
            }
        }

        if (BuildConfig.DEBUG.not()) {
            val jankStats: JankStats = JankStats
                .createAndTrack(window) { frameData ->
                    if (frameData.isJank) {
                        val states = frameData.states.joinToString { "${it.key}:${it.value}" }

                        Log.w(
                            "Jank",
                            "Jank states[$states] ${TimeUnit.NANOSECONDS.toMillis(frameData.frameDurationUiNanos)}ms"
                        )
                    }
                }

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    try {
                        jankStats.isTrackingEnabled = true
                        awaitCancellation()
                    } finally {
                        jankStats.isTrackingEnabled = false
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch { mainViewModel.newIntentChannel.send(intent) }
    }
}
