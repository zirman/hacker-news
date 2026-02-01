package com.monoid.hackernews

import androidx.activity.ComponentActivity

fun ComponentActivity.jankStats() {
    return
    // TODO: only enable in release builds
//    if (BuildConfig.BUILD_TYPE != "release") return
//    val jankStats: JankStats = JankStats.createAndTrack(window) { frameData ->
//        if (frameData.isJank) {
//            val states = frameData.states.joinToString { "${it.key}:${it.value}" }
//
//            Log.w(
//                /* tag = */
//                "Jank",
//                /* msg = */
//                "Jank states[$states] ${
//                    TimeUnit.NANOSECONDS.toMillis(frameData.frameDurationUiNanos)
//                }ms",
//            )
//        }
//    }
//    lifecycleScope.launch {
//        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
//            try {
//                jankStats.isTrackingEnabled = true
//                awaitCancellation()
//            } finally {
//                jankStats.isTrackingEnabled = false
//            }
//        }
//    }
}
