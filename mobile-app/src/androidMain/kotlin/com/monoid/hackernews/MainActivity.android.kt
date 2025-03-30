package com.monoid.hackernews

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.model.LightDarkMode
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.view.App
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    private val logger: LoggerAdapter by inject()
    private val repository: SettingsRepository by inject()

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        windowSetup()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
        jankStats()
        lifecycleScope.launch(context) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.preferences
                    .distinctUntilChangedBy { it.lightDarkMode }
                    .collect { preferences -> setSystemBarsAppearance(preferences.lightDarkMode) }
            }
        }
    }

    private fun setSystemBarsAppearance(lightDarkMode: LightDarkMode) {
        val darkMode: Boolean = when (lightDarkMode) {
            LightDarkMode.System ->
                resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

            LightDarkMode.Light -> false
            LightDarkMode.Dark -> true
        }
        val systemBarStyle = if (darkMode) {
            SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            )
        }
        enableEdgeToEdge(
            navigationBarStyle = systemBarStyle,
            statusBarStyle = systemBarStyle,
        )
    }

    private fun windowSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen().apply {
                setKeepOnScreenCondition {
                    false
                }
                var startedAnimation = false
                setOnExitAnimationListener { splashScreenView ->
                    // Work around for showing a blank screen bug when resuming activity
                    // Animation cannot be done a second time because accessing the icon gets
                    // an NPE
                    if (startedAnimation) {
                        splashScreenView.remove()
                        setSystemBarsAppearance(repository.preferences.value.lightDarkMode)
                        return@setOnExitAnimationListener
                    }
                    try {
                        val animateIn = ObjectAnimator.ofPropertyValuesHolder(
                            splashScreenView.iconView,
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f),
                        )
                        animateIn.interpolator = AnticipateInterpolator()
                        animateIn.duration = 400L
                        animateIn.doOnEnd {
                            splashScreenView.remove()
                            setSystemBarsAppearance(repository.preferences.value.lightDarkMode)
                        }
                        animateIn.start()
                    } catch (throwable: Throwable) {
                        logger.recordException(
                            messageString = "CoroutineExceptionHandler",
                            throwable = throwable,
                            tag = TAG,
                        )
                    } finally {
                        startedAnimation = true
                    }
                }
            }
        }
    }

    private fun jankStats() {
        if (false) { // TODO: prod build
            val jankStats: JankStats = JankStats
                .createAndTrack(window) { frameData ->
                    if (frameData.isJank) {
                        val states = frameData.states.joinToString { "${it.key}:${it.value}" }

                        Log.w(
                            /* tag = */
                            "Jank",
                            /* msg = */
                            "Jank states[$states] ${
                                TimeUnit.NANOSECONDS.toMillis(frameData.frameDurationUiNanos)
                            }ms",
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

    companion object {
        private const val TAG = "MainActivity"
    }
}
