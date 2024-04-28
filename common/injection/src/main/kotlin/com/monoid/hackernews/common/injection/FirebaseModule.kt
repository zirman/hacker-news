package com.monoid.hackernews.common.injection

import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.dsl.module

val firebaseModule = module {
    single {
        FirebaseCrashlytics.getInstance()
    }
}
