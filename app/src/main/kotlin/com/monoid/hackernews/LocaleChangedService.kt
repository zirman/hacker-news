package com.monoid.hackernews

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LocaleChangedService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateAndPushDynamicShortcuts()
        stopSelf()
        return START_NOT_STICKY
    }
}
