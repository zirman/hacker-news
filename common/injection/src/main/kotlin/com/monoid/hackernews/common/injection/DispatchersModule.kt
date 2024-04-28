package com.monoid.hackernews.common.injection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class DispatcherQualifier {
    Default,
    Io,
    Unconfined,
}

val dispatcherModule = module {
    single { Dispatchers.Main }
    single<CoroutineDispatcher>(named(DispatcherQualifier.Default)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(DispatcherQualifier.Io)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(DispatcherQualifier.Unconfined)) { Dispatchers.Unconfined }
}
