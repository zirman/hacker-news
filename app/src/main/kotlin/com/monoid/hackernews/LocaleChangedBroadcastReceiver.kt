package com.monoid.hackernews

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocaleChangedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        context.updateAndPushDynamicShortcuts()
    }
}
