package com.monoid.hackernews.common.data.room

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.monoid.hackernews.common.data.model.ItemType

@Database(
    entities = [
        TrendingStoryDb::class,
        NewStoryDb::class,
        HotStoryDb::class,
        ShowStoryDb::class,
        AskStoryDb::class,
        JobStoryDb::class,
        ItemDb::class,
        UserDb::class,
        CommentDb::class,
    ],
    version = 3,
    exportSchema = true,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 2, to = 3),
//    ],
)
@ColumnTypeConverters(ItemType.Converter::class)
@ConstructedBy(HNDatabaseConstructor::class)
abstract class HNDatabase : RoomDatabase() {
    abstract fun topStoryDao(): TrendingStoryDao
    abstract fun newStoryDao(): NewStoryDao
    abstract fun bestStoryDao(): HotStoryDao
    abstract fun showStoryDao(): ShowStoryDao
    abstract fun askStoryDao(): AskStoryDao
    abstract fun jobStoryDao(): JobStoryDao
    abstract fun itemDao(): ItemDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object HNDatabaseConstructor : RoomDatabaseConstructor<HNDatabase> {
    override fun initialize(): HNDatabase
}
