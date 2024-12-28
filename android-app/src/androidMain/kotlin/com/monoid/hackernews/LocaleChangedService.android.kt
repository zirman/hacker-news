package com.monoid.hackernews

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.koin.android.scope.AndroidScopeComponent
import org.koin.android.scope.createServiceScope
import org.koin.core.scope.Scope

class LocaleChangedService : Service(), AndroidScopeComponent {
    override val scope: Scope = createServiceScope()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO
        // updateAndPushDynamicShortcuts(MainActivity::class.java)
        stopSelf()
        return START_NOT_STICKY
    }
}
