package com.monoid.hackernews

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocaleChangedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Starting a service gets always updates resources. Otherwise it does not always get
        // updated locale in resources. Why?
        context.startService(Intent(context, LocaleChangedService::class.java))
    }
}
