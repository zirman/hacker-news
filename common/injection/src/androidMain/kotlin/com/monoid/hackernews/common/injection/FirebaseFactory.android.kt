package com.monoid.hackernews.common.injection

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual fun loggerFactory(): LoggerAdapter {
    return LoggerAdapterImpl(FirebaseCrashlytics.getInstance())
}
