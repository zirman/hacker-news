package com.monoid.hackernews.common

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.injection.DispatcherQualifier
import com.monoid.hackernews.common.room.HNDatabase
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single {
        Room.databaseBuilder<HNDatabase>("hndatabase.db")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(get<CoroutineDispatcher>(named(DispatcherQualifier.Io)))
            .build()
    }

    includes(databaseDaoModule)
}
