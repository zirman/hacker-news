package com.monoid.hackernews.wear

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ReportDrawnWhen
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.wear.compose.material.Text
import com.monoid.hackernews.common.core.metro.ActivityScope
import com.monoid.hackernews.common.core.metro.ContributesActivityInjector
import com.monoid.hackernews.common.core.metro.metroViewModel
import com.monoid.hackernews.common.view.stories.LocalPlatformContext
import com.monoid.hackernews.common.view.stories.PlatformContext
import com.monoid.hackernews.jankStats
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@Inject
class WearMainActivity(
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory,
) : ComponentActivity() {
    @ContributesTo(ActivityScope::class)
    @BindingContainer
    interface InnerBindings {
        @Binds
        fun bindActivity(activity: WearMainActivity): Activity
    }

    interface Injectors {
        @ContributesActivityInjector
        fun target(): WearMainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var contentComposed by remember { mutableStateOf(false) }
            ReportDrawnWhen { contentComposed }
            SideEffect { contentComposed = true }
            val mainViewModel: WearMainViewModel = metroViewModel()
            CompositionLocalProvider(
                LocalPlatformContext provides PlatformContext(LocalContext.current),
            ) {
                WearHackerNewsTheme {
                    Text("Hello World")
                }
            }
        }
        jankStats()
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        lifecycleScope.launch { viewModel.newIntentChannel.send(intent) }
//    }
}
