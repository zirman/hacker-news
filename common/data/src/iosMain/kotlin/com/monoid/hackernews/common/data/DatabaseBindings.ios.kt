@file:OptIn(ExperimentalForeignApi::class)

package com.monoid.hackernews.common.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.data.room.HNDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@BindingContainer(includes = [DatabaseDaoBindings::class])
class IosDatabaseBindings() {

    @SingleIn(AppScope::class)
    @Provides
    fun providesHnDatabase(
        @Named("IoDispatcherQualifier")
        coroutineDispatcher: CoroutineDispatcher,
    ): HNDatabase {
        val documentDirectory = NSFileManager.defaultManager
            .URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            ?.path
            .let { checkNotNull(it) }
        Room
            .databaseBuilder<HNDatabase>(name = "$documentDirectory/$DATABASE_FILE_NAME")
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
            .run { return this }
    }
}
