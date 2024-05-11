package com.monoid.hackernews.common.injection

import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseAdapterImpl(val firebaseCrashlytics: FirebaseCrashlytics) : FirebaseAdapter {
    override fun recordException(throwable: Throwable) {
        firebaseCrashlytics.recordException(throwable)
    }
}
