package com.monoid.hackernews.common.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TopStoryDb::class,
        NewStoryDb::class,
        BestStoryDb::class,
        ShowStoryDb::class,
        AskStoryDb::class,
        JobStoryDb::class,
        ItemDb::class,
        UserDb::class,
        UpvoteDb::class,
        FavoriteDb::class,
        FlagDb::class,
        ExpandedDb::class,
        FollowedDb::class,
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class HNDatabase : RoomDatabase() {
    abstract fun topStoryDao(): TopStoryDao
    abstract fun newStoryDao(): NewStoryDao
    abstract fun bestStoryDao(): BestStoryDao
    abstract fun showStoryDao(): ShowStoryDao
    abstract fun askStoryDao(): AskStoryDao
    abstract fun jobStoryDao(): JobStoryDao
    abstract fun itemDao(): ItemDao
    abstract fun userDao(): UserDao
    abstract fun upvoteDao(): UpvoteDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun flagDao(): FlagDao
    abstract fun expandedDao(): ExpandedDao
    abstract fun followedDao(): FollowedDao
}
