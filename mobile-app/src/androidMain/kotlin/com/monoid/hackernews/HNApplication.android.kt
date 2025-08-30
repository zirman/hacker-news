package com.monoid.hackernews

import android.app.Application
import android.app.UiModeManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.model.LightDarkMode
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.view.ApplicationModule
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.ksp.generated.module

class HNApplication : Application() {
    private val remoteDataSource: HttpClient by inject()
    private val database: HNDatabase by inject()
    private val settings: SettingsRepository by inject()
    private val lifecycleOwner: LifecycleOwner by inject(named<ProcessLifecycleOwner>())
    private val logger: LoggerAdapter by inject()

    private val context = Dispatchers.Main.immediate + CoroutineExceptionHandler { coroutineContext, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    override fun onCreate() {
        super.onCreate()
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
        startKoin {
            androidContext(this@HNApplication)
            androidLogger()
            androidFileProperties()
            modules(ApplicationModule().module)
        }
        // TODO
        // updateAndPushDynamicShortcuts(MainActivity::class.java)
        // register locale changed broadcast receiver
        registerReceiver(
            /* receiver = */
            LocaleChangedBroadcastReceiver(),
            /* filter = */
            IntentFilter(Intent.ACTION_LOCALE_CHANGED),
        )
        lifecycleOwner.lifecycleScope.launch(context) {
            settings.preferences
                .distinctUntilChangedBy { it.lightDarkMode }
                .collect {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        checkNotNull(getSystemService<UiModeManager>())
                            .setApplicationNightMode(
                                when (it.lightDarkMode) {
                                    LightDarkMode.System -> UiModeManager.MODE_NIGHT_AUTO
                                    LightDarkMode.Light -> UiModeManager.MODE_NIGHT_NO
                                    LightDarkMode.Dark -> UiModeManager.MODE_NIGHT_YES
                                }
                            )
                    } else {
                        AppCompatDelegate.setDefaultNightMode(
                            when (it.lightDarkMode) {
                                LightDarkMode.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                                LightDarkMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
                                LightDarkMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
                            }
                        )
                    }
                }
        }
    }

    override fun onTerminate() {
        remoteDataSource.close()
        database.close()
        super.onTerminate()
    }
}

private const val TAG = "HNApplication"
