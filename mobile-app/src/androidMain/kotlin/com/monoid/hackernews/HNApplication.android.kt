package com.monoid.hackernews

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import dev.zacsweers.metro.createGraphFactory

class HNApplication : Application() {
    val appGraph by lazy { createGraphFactory<AndroidAppGraph.Factory>().create(this) }

    override fun onCreate() {
        super.onCreate()
        // force creation of singleton UiModeConfigurator
        appGraph.uiModeConfigurator
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
        // register locale changed broadcast receiver
        registerReceiver(
            /* receiver = */
            LocaleChangedBroadcastReceiver(),
            /* filter = */
            IntentFilter(Intent.ACTION_LOCALE_CHANGED),
        )
    }

    override fun onTerminate() {
        appGraph.httpClient.close()
        appGraph.db.close()
        super.onTerminate()
    }
}
