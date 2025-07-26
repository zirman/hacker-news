package com.monoid.hackernews

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.view.App
import io.ktor.http.Url
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.scope.Scope
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()
    private val logger: LoggerAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        windowSetup(savedInstanceState)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App(onClickUrl = ::onClickUrl) }
        jankStats()
    }

    private fun windowSetup(savedInstanceState: Bundle?) {
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
                    if (savedInstanceState != null || startedAnimation) {
                        splashScreenView.remove()
                        enableEdgeToEdge()
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
                            enableEdgeToEdge()
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

    private fun onClickUrl(url: Url) {
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = url.toString().toUri()
                },
            )
        } catch (throwable: Throwable) {
            logger.recordException(
                messageString = "onClickUrl($url)",
                throwable = throwable,
                tag = TAG,
            )
        }
    }
}

private const val TAG = "MainActivity"
