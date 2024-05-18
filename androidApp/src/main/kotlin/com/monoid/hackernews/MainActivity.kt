@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.monoid.hackernews

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.monoid.hackernews.common.data.Authentication
import com.monoid.hackernews.common.injection.FirebaseAdapter
import com.monoid.hackernews.common.ui.util.rememberUseDarkTheme
import com.monoid.hackernews.view.main.MainContent
import com.monoid.hackernews.view.theme.AppTheme
import com.monoid.hackernews.view.theme.HNFont
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    private val authentication: DataStore<Authentication> by inject()
    private val viewModel: MainViewModel by inject()
    private val crashlytics: FirebaseAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = getColor(android.R.color.transparent)
        window.navigationBarColor = getColor(android.R.color.transparent)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                false
            }

            setOnExitAnimationListener { splashScreenView ->
                try {
                    window.statusBarColor = getColor(android.R.color.transparent)
                    window.navigationBarColor = getColor(android.R.color.transparent)

                    val animateIn = ObjectAnimator.ofPropertyValuesHolder(
                        splashScreenView.iconView,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f),
                    )

                    animateIn.interpolator = AnticipateInterpolator()
                    animateIn.duration = 400L

                    animateIn.doOnEnd {
                        splashScreenView.remove()

                        window.insetsController?.setSystemBarsAppearance(
                            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                                Configuration.UI_MODE_NIGHT_NO ->
                                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS

                                else -> 0
                            },
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                        )
                    }

                    animateIn.start()
                } catch (error: Throwable) {
                    crashlytics.recordException(error)
                }
            }
        }

        window.insetsController?.setSystemBarsAppearance(
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO ->
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS

                else -> 0
            },
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )

        setContent {
            val useDarkTheme: Boolean =
                rememberUseDarkTheme()

            val fontState: String? = null
//            by remember<Flow<String?>> { authentication.data.map { it.font } }
//                .collectAsStateWithLifecycle(null)

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
                MainContent(windowSizeClass = calculateWindowSizeClass(this))
            }
        }

        if (false) { // TODO: prod build
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
        lifecycleScope.launch { viewModel.newIntentChannel.send(intent) }
    }
}
