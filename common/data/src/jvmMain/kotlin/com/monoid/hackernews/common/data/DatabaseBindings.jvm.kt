package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.data.room.HNDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File

@ContributesTo(AppScope::class)
@BindingContainer(includes = [DatabaseDaoBindings::class])
object JvmDatabaseBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesHnDatabase(
        @Named("IoDispatcherQualifier")
        coroutineDispatcher: CoroutineDispatcher,
    ): HNDatabase {
        Room
            .databaseBuilder<HNDatabase>(
                name = File(
                    System.getProperty("java.io.tmpdir"),
                    DATABASE_FILE_NAME,
                ).absolutePath,
            )
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
            .run { return this }
    }
}
