package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.core.DispatcherQualifier
import com.monoid.hackernews.common.data.room.HNDatabase
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
        Room
            .databaseBuilder<HNDatabase>(
                name = File(
                    System.getProperty("java.io.tmpdir"),
                    DATABASE_FILE_NAME,
                ).absolutePath,
            )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
            .run { return this }
    }
}
