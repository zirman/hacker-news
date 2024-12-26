package com.monoid.hackernews.common.injection

import com.google.firebase.crashlytics.FirebaseCrashlytics
import co.touchlab.kermit.Logger

class LoggerAdapterImpl(val firebaseCrashlytics: FirebaseCrashlytics) : LoggerAdapter {
    override fun recordException(messageString: String, throwable: Throwable, tag: String?) {
        if (tag != null) {
            Logger.e(messageString = messageString, throwable = throwable, tag)
        } else {
            Logger.e(messageString = messageString, throwable = throwable)
        }

        firebaseCrashlytics.recordException(throwable)
    }
}
