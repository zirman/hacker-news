package com.monoid.hackernews.common.view

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.model.LightDarkMode
import com.monoid.hackernews.common.data.model.SettingsRepository
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import org.koin.core.annotation.Named

class UiModeConfigurator(
    context: Context,
    @Named(type = ProcessLifecycleOwner::class)
    lifecycleOwner: LifecycleOwner,
    settings: SettingsRepository,
    logger: LoggerAdapter,
) {
    init {
        lifecycleOwner.lifecycleScope.launch {
            try {
                settings.preferences
                    .distinctUntilChangedBy { it.lightDarkMode }
                    .collect {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            checkNotNull(context.getSystemService<UiModeManager>())
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
            } catch (throwable: Throwable) {
                logger.recordException(TAG, throwable)
            }
        }
    }
}

private const val TAG = "UiModeConfigurator"
