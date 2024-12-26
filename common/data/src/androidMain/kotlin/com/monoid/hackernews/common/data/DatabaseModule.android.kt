package com.monoid.hackernews.common.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.injection.DispatcherQualifier
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module(includes = [DatabaseDaoModule::class])
actual class DatabaseModule {

    @Single
    fun hnDatabase(
        context: Context,
        @Named(type = DispatcherQualifier.Io::class)
        coroutineDispatcher: CoroutineDispatcher,
    ): HNDatabase {
        return Room
            .databaseBuilder<HNDatabase>(
                context = context,
                name = context.getDatabasePath(databaseFileName).absolutePath,
                //factory = { HNDatabase::class.instantiateImpl() },
            )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(coroutineDispatcher)
            .build()
    }
}
