package com.monoid.hackernews

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.monoid.hackernews.common.data.BuildConfig
import com.monoid.hackernews.common.datastore.Authentication
import com.monoid.hackernews.common.ui.util.rememberUseDarkTheme
import com.monoid.hackernews.view.main.MainContent
import com.monoid.hackernews.view.theme.AppTheme
import com.monoid.hackernews.view.theme.HNFont
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authentication: DataStore<Authentication>

    @Inject
    lateinit var newIntentChannel: Channel<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen().setOnExitAnimationListener { splashScreenView ->
            window.statusBarColor = getColor(android.R.color.transparent)
            window.navigationBarColor = getColor(android.R.color.transparent)

            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.view.height.toFloat()
            )

            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 400L

            slideUp.doOnEnd {
                splashScreenView.remove()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_NO
                ) {
                    window.insetsController?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            }

            slideUp.start()
        }

        setContent {
            val useDarkTheme: Boolean =
                rememberUseDarkTheme()

            val fontState: String? by remember<Flow<String?>> { authentication.data.map { it.font } }
                .collectAsStateWithLifecycle(null)

            AppTheme(
                useDarkTheme = useDarkTheme,
                hnFont = remember(fontState) {
                    fontState
                        ?.let { font ->
                            try {
                                Json.decodeFromString<HNFont>(font)
                            } catch (error: Throwable) {
                                null
                            }
                        }
                        ?: HNFont.Default
                }
            ) {
                val systemUiController: SystemUiController =
                    rememberSystemUiController()

                val primaryColor = MaterialTheme.colorScheme.primary

                SideEffect {
                    systemUiController.setStatusBarColor(
                        // Opaque color is used for window bar on Chrome OS.
                        color = primaryColor.copy(alpha = 0f),
                        darkIcons = useDarkTheme.not()
                    )

                    systemUiController.setNavigationBarColor(
                        // if alpha is zero, color is ignored
                        color = Color(red = 128, green = 128, blue = 128, alpha = 1),
                        darkIcons = useDarkTheme.not()
                    )
                }

                MainContent(
                    mainViewModel = hiltViewModel(),
                    windowSizeClass = calculateWindowSizeClass(this)
                )
            }
        }

        if (BuildConfig.DEBUG.not()) {
            val jankStats: JankStats = JankStats
                .createAndTrack(window) { frameData ->
                    if (frameData.isJank) {
                        val states = frameData.states.joinToString { "${it.key}:${it.value}" }

                        Log.w(
                            /* tag = */ "Jank",
                            /* msg = */ "Jank states[$states] ${
                                TimeUnit.NANOSECONDS.toMillis(frameData.frameDurationUiNanos)
                            }ms"
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
        lifecycleScope.launch { newIntentChannel.send(intent) }
    }
}
