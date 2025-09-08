package com.monoid.hackernews

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.monoid.hackernews.common.view.BuildConfig
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

fun ComponentActivity.jankStats() {
    @Suppress("KotlinConstantConditions")
    if (BuildConfig.BUILD_TYPE != "release") return
    val jankStats: JankStats = JankStats.createAndTrack(window) { frameData ->
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
