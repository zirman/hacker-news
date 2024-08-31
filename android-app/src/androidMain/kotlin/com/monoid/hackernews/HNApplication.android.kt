package com.monoid.hackernews

//import com.monoid.hackernews.common.view.updateAndPushDynamicShortcuts
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.StrictMode
import com.monoid.hackernews.common.data.dataStoreModule
import com.monoid.hackernews.common.data.databaseModule
import com.monoid.hackernews.common.data.networkModule
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.injection.dispatcherModule
import com.monoid.hackernews.common.injection.loggerModule
import io.ktor.client.HttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class HNApplication : Application() {
    private val remoteDataSource: HttpClient by inject()
    private val database: HNDatabase by inject()

    override fun onCreate() {
        super.onCreate()

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectUnsafeIntentLaunch()
                .build()
        )

        startKoin {
            androidContext(this@HNApplication)
            androidLogger()
            androidFileProperties()
            modules(
                applicationModule,
                dispatcherModule,
                networkModule,
                databaseModule,
                dataStoreModule,
                loggerModule,
            )
        }

//        updateAndPushDynamicShortcuts(MainActivity::class.java)

        // register locale changed broadcast receiver
        registerReceiver(
            /* receiver = */
            LocaleChangedBroadcastReceiver(),
            /* filter = */
            IntentFilter(Intent.ACTION_LOCALE_CHANGED),
        )
    }

    override fun onTerminate() {
        remoteDataSource.close()
        database.close()
        super.onTerminate()
    }
}
