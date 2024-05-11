package com.monoid.hackernews.common.injection

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual fun firebaseFactory(): FirebaseAdapter {
    return FirebaseAdapterImpl(FirebaseCrashlytics.getInstance())
}
