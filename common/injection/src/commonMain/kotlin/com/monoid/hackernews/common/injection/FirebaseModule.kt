package com.monoid.hackernews.common.injection

import org.koin.dsl.module

val loggerModule = module {
    single {
        loggerFactory()
    }
}
