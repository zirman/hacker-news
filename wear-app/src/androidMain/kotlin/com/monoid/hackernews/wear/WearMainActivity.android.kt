package com.monoid.hackernews.wear

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ReportDrawnWhen
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.wear.compose.material.Text
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ActivityKey
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.jankStats
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(WearMainActivity::class)
@Inject
class WearMainActivity(
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory,
    private val logger: LoggerAdapter,
) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var contentComposed by remember { mutableStateOf(false) }
            ReportDrawnWhen { contentComposed }
            SideEffect { contentComposed = true }
            val mainViewModel: WearMainViewModel = metroViewModel()
            WearHackerNewsTheme {
                Text("Hello World")
            }
        }
        jankStats()
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        lifecycleScope.launch { viewModel.newIntentChannel.send(intent) }
//    }
}
