package com.monoid.hackernews

import com.monoid.hackernews.common.view.ApplicationModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

@Suppress("unused")
fun initKoin() {
    startKoin {
        modules(ApplicationModule().module)
    }
}
