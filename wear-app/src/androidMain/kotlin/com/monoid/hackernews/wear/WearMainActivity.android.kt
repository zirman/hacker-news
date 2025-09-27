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
import com.monoid.hackernews.common.core.metro.ActivityGraph
import com.monoid.hackernews.common.core.metro.ActivityKey
import com.monoid.hackernews.common.core.metro.ActivityScope
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.jankStats
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@Inject
class WearMainActivity(
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory,
    private val logger: LoggerAdapter,
) : ComponentActivity() {
    @GraphExtension(ActivityScope::class)
    interface Graph : ActivityGraph {
        @Binds
        fun bindActivity(activity: WearMainActivity): Activity

        @ContributesTo(AppScope::class)
        @GraphExtension.Factory()
        interface Factory {
            fun createWearMainActivityGraph(): Graph
        }
    }

    @ContributesTo(AppScope::class)
    @BindingContainer
    object AppBindings {
        @ActivityKey(WearMainActivity::class)
        @IntoMap
        @Provides
        fun provideActivityGraph(graphFactory: Graph.Factory): ActivityGraph =
            graphFactory.createWearMainActivityGraph()
    }

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
