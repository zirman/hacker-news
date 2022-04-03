package com.monoid.hackernews.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TopStory::class,
        NewStory::class,
        BestStory::class,
        ShowStory::class,
        AskStory::class,
        JobStory::class,
        Item::class,
        User::class,
        Upvote::class,
        Favorite::class,
    ],
    version = 1,
    exportSchema = false,
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
}
