package com.monoid.hackernews

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.monoid.hackernews.common.core.log.LoggerAdapter

fun ComponentActivity.setupSplashScreen(savedInstanceState: Bundle?, logger: LoggerAdapter, tag: String) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    installSplashScreen().run {
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
                    tag = tag,
                )
            } finally {
                startedAnimation = true
            }
        }
    }
}
