package com.monoid.hackernews

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.ProfilingManager
import android.os.ProfilingResult
import android.os.ProfilingTrigger
import android.os.StrictMode
import androidx.core.content.ContextCompat
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.view.UiModeConfigurator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
@Inject
class HackerNewsApplication(
    uiModeConfigurator: Lazy<UiModeConfigurator>,
    httpClient: Lazy<HttpClient>,
    db: Lazy<HNDatabase>,
) : Application() {
    val uiModeConfigurator by uiModeConfigurator
    val httpClient by httpClient
    val db by db

    override fun onCreate() {
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        it.detectUnsafeIntentLaunch()
                    } else {
                        it
                    }
                }
                .build(),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            val profilingManager = applicationContext.getSystemService(ProfilingManager::class.java)
            profilingManager.registerForAllProfilingResults(
                ContextCompat.getMainExecutor(this),
                { profilingResult ->
                    if (profilingResult.errorCode == ProfilingResult.ERROR_NONE &&
                        profilingResult.triggerType == ProfilingTrigger.TRIGGER_TYPE_OOM
                    ) {
                        // upload profilingResult.resultFilePath
                    }
                },
            )
            profilingManager.addProfilingTriggers(
                listOf(
                    ProfilingTrigger.Builder(ProfilingTrigger.TRIGGER_TYPE_OOM).build(),
                ),
            )
        }
        // force creation of singleton UiModeConfigurator
        uiModeConfigurator
        super.onCreate()
        // register locale changed broadcast receiver
        registerReceiver(
            /* receiver = */
            LocaleChangedBroadcastReceiver(),
            /* filter = */
            IntentFilter(Intent.ACTION_LOCALE_CHANGED),
        )
    }

    override fun onTerminate() {
        httpClient.close()
        db.close()
        super.onTerminate()
    }
}
