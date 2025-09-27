package com.monoid.hackernews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ReportDrawnWhen
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ActivityGraph
import com.monoid.hackernews.common.core.metro.ActivityKey
import com.monoid.hackernews.common.core.metro.ActivityScope
import com.monoid.hackernews.common.view.App
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.http.Url

@SingleIn(ActivityScope::class)
@Inject
class MainActivity(
    private val logger: LoggerAdapter,
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory,
) : ComponentActivity() {
    @GraphExtension(ActivityScope::class)
    interface Graph : ActivityGraph {
        @Binds
        fun bindActivity(activity: MainActivity): Activity

        @ContributesTo(AppScope::class)
        @GraphExtension.Factory
        interface Factory {
            fun createMainActivityGraph(): Graph
        }
    }

    @ContributesTo(AppScope::class)
    @BindingContainer
    object AppBindings {
        @ActivityKey(MainActivity::class)
        @IntoMap
        @Provides
        fun provideActivityGraph(graphFactory: Graph.Factory): ActivityGraph =
            graphFactory.createMainActivityGraph()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen(
            savedInstanceState = savedInstanceState,
            logger = logger,
            tag = TAG,
        )
        super.onCreate(savedInstanceState)
        setContent {
            var contentComposed by remember { mutableStateOf(false) }
            ReportDrawnWhen { contentComposed }
            SideEffect { contentComposed = true }
            App(onClickUrl = ::onClickUrl)
        }
        jankStats()
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
