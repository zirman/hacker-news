package com.monoid.hackernews.wear

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.monoid.hackernews.shared.data.TopStoryRepository
import com.monoid.hackernews.shared.domain.LiveUpdateUseCase
import com.monoid.hackernews.shared.view.R
import com.monoid.hackernews.wear.theme.HackerNewsTheme
import com.monoid.hackernews.wear.view.home.HomeScreen
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HackerNewsTheme {
                HomeScreen(
                    mainViewModel = mainViewModel,
                    title = stringResource(id = R.string.top_stories),
                    orderedItemRepo = remember(mainViewModel) {
                        LiveUpdateUseCase(
                            getSystemService()!!,
                            TopStoryRepository(
                                httpClient = mainViewModel.httpClient,
                                topStoryDao = mainViewModel.topStoryDao
                            )
                        )
                    },
                    onSelectItemId = { /*TODO*/ }
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
