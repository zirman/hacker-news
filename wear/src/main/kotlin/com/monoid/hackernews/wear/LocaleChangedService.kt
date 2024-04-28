package com.monoid.hackernews.wear

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.monoid.hackernews.common.view.updateAndPushDynamicShortcuts
import org.koin.android.scope.AndroidScopeComponent
import org.koin.android.scope.createServiceScope
import org.koin.core.scope.Scope

class LocaleChangedService : Service(), AndroidScopeComponent {
    override val scope: Scope = createServiceScope()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateAndPushDynamicShortcuts(MainActivity::class.java)
        stopSelf()
        return START_NOT_STICKY
    }
}
