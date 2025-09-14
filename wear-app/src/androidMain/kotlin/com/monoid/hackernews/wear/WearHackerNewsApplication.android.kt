package com.monoid.hackernews.wear

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import dev.zacsweers.metro.createGraphFactory

class WearHackerNewsApplication : Application() {
    val appGraph by lazy { createGraphFactory<WearAppGraph.Factory>().create(this) }

    override fun onCreate() {
        super.onCreate()
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
