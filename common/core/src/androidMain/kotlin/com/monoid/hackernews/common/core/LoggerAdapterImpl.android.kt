package com.monoid.hackernews.common.core

import co.touchlab.kermit.Logger
import com.google.firebase.crashlytics.FirebaseCrashlytics

class LoggerAdapterImpl(private val firebaseCrashlytics: FirebaseCrashlytics) : LoggerAdapter {
    override fun recordException(messageString: String, throwable: Throwable, tag: String?) {
        if (tag != null) {
            Logger.e(messageString = messageString, throwable = throwable, tag)
        } else {
            Logger.e(messageString = messageString, throwable = throwable)
        }

        firebaseCrashlytics.recordException(throwable)
    }
}
