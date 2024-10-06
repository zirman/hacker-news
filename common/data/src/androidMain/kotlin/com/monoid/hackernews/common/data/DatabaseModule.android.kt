package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.injection.DispatcherQualifier
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single {
        val context = androidContext()
        val dbFile = context.getDatabasePath("hacker-news-database.db")
        Room.databaseBuilder<HNDatabase>(
            context = context,
            name = dbFile.absolutePath,
            //factory = { HNDatabase::class.instantiateImpl() },
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(get<CoroutineDispatcher>(named(DispatcherQualifier.Io)))
            .build()
    }

    includes(databaseDaoModule)
}
