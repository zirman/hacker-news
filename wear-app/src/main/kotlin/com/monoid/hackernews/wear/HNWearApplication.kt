package com.monoid.hackernews.wear

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.monoid.hackernews.common.data.room.HNDatabase
import io.ktor.client.HttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class HNWearApplication : Application() {
    private val db: HNDatabase by inject()
    private val httpClient: HttpClient by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HNWearApplication)
            androidLogger()
            androidFileProperties()
            modules(WearApplicationModule().module)
        }

//        updateAndPushDynamicShortcuts(MainActivity::class.java)

        // register locale changed broadcast receiver
        registerReceiver(
            LocaleChangedBroadcastReceiver(),
            IntentFilter(Intent.ACTION_LOCALE_CHANGED)
        )
    }

    override fun onTerminate() {
        httpClient.close()
        db.close()
        super.onTerminate()
    }
}
