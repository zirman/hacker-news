package com.monoid.hackernews

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.monoid.hackernews.common.core.metro.ContributesServiceInjector
import com.monoid.hackernews.common.core.metro.ServiceScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(ServiceScope::class)
@Inject
class LocaleChangedService : Service() {
    @ContributesTo(ServiceScope::class)
    @BindingContainer
    interface InnerBindings {
        @Binds
        val LocaleChangedService.bind: Service
    }

    interface Injectors {
        @ContributesServiceInjector
        fun target(): LocaleChangedService
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        updateAndPushDynamicShortcuts(MainActivity::class.java)
        stopSelf()
        return START_NOT_STICKY
    }
}
