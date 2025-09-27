package com.monoid.hackernews.wear

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.view.UiModeConfigurator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class WearHackerNewsApplication(
    uiModeConfigurator: Lazy<UiModeConfigurator>,
    httpClient: Lazy<HttpClient>,
    db: Lazy<HNDatabase>,
) : Application() {
    val uiModeConfigurator by uiModeConfigurator
    val httpClient by httpClient
    val db by db

    override fun onCreate() {
        // updateAndPushDynamicShortcuts(MainActivity::class.java)
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
