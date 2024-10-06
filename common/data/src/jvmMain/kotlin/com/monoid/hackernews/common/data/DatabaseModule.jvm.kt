package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.injection.DispatcherQualifier
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

actual val databaseModule: Module = module {
    single {
        Room
            .databaseBuilder<HNDatabase>(
                name = File(
                    System.getProperty("java.io.tmpdir"),
                    "hndatabase.db",
                ).absolutePath,
            )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(get<CoroutineDispatcher>(named(DispatcherQualifier.Io)))
            .build()
    }

    includes(databaseDaoModule)
}
