package com.monoid.hackernews.common.injection

import org.koin.dsl.module

val firebaseModule = module {
    single {
        firebaseFactory()
    }
}
