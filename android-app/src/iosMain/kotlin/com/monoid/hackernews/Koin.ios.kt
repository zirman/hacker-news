package com.monoid.hackernews

import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin() {
    startKoin {
        modules(ApplicationModule().modules)
    }
}
