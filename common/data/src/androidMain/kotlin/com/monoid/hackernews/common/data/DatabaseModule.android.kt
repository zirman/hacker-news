package com.monoid.hackernews.common.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.core.IoDispatcherQualifier
import com.monoid.hackernews.common.data.room.HNDatabase
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module(includes = [DatabaseDaoModule::class])
actual class DatabaseModule {

    @Single
    fun hnDatabase(
        context: Context,
        @Named(type = IoDispatcherQualifier::class)
        coroutineDispatcher: CoroutineDispatcher,
    ): HNDatabase {
        return Room
            .databaseBuilder<HNDatabase>(
                context = context,
                name = context.getDatabasePath(DATABASE_FILE_NAME).absolutePath,
                // factory = { HNDatabase::class.instantiateImpl() },
            )
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
    }
}
