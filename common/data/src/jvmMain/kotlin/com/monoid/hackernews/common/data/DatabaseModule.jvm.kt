package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.injection.DispatcherQualifier
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.File

@Module(includes = [DatabaseDaoModule::class])
actual class DatabaseModule {

    @Single
    fun hnDatabase(
        @Named(type = DispatcherQualifier.Io::class)
        coroutineDispatcher: CoroutineDispatcher
    ): HNDatabase {
        return Room
            .databaseBuilder<HNDatabase>(
                name = File(
                    System.getProperty("java.io.tmpdir"),
                    "hn_database.db",
                ).absolutePath,
            )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
    }
}
