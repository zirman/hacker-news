package com.monoid.hackernews.wear

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ReportDrawnWhen
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.metrics.performance.JankStats
import com.monoid.hackernews.common.data.BuildConfig
import com.monoid.hackernews.common.domain.LiveUpdateUseCase
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.wear.theme.HackerNewsTheme
import com.monoid.hackernews.wear.view.home.HomeScreen
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var contentComposed: Boolean by remember {
                mutableStateOf(false)
            }

            ReportDrawnWhen {
                contentComposed
            }

            val mainViewModel: MainViewModel = koinViewModel()

            HackerNewsTheme {
                HomeScreen(
                    itemTreeRepository = mainViewModel.itemTreeRepository,
                    title = stringResource(id = R.string.top_stories),
                    orderedItemRepo = remember(mainViewModel.topStoryRepository) {
                        LiveUpdateUseCase(
                            getSystemService()!!,
                            mainViewModel.topStoryRepository,
                        )
                    },
                    onSelectItemId = { /*TODO*/ },
                )
            }

            contentComposed = true
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
        lifecycleScope.launch { viewModel.newIntentChannel.send(intent) }
    }
}
