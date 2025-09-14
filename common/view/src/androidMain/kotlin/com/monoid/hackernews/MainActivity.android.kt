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
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ActivityKey
import com.monoid.hackernews.common.core.metro.AndroidViewModelFactory
import com.monoid.hackernews.common.view.App
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import io.ktor.http.Url

@ContributesIntoMap(AppScope::class, binding<Activity>())
@ActivityKey(MainActivity::class)
@Inject
class MainActivity(
    override val defaultViewModelProviderFactory: AndroidViewModelFactory,
    private val logger: LoggerAdapter,
) : ComponentActivity() {
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
