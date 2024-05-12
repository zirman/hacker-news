package com.monoid.hackernews.common

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.injection.DispatcherQualifier
import com.monoid.hackernews.common.room.HNDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single {
        val context = androidContext()
        val dbFile = context.getDatabasePath("hacker-news-database")
        Room.databaseBuilder<HNDatabase>(context, dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(get<CoroutineDispatcher>(named(DispatcherQualifier.Io)))
            .build()
    }

    includes(databaseDaoModule)
}
