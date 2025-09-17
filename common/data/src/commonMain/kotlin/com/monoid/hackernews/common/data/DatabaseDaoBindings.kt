package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.data.room.AskStoryDao
import com.monoid.hackernews.common.data.room.CommentDao
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.data.room.HotStoryDao
import com.monoid.hackernews.common.data.room.ItemDao
import com.monoid.hackernews.common.data.room.JobStoryDao
import com.monoid.hackernews.common.data.room.NewStoryDao
import com.monoid.hackernews.common.data.room.ShowStoryDao
import com.monoid.hackernews.common.data.room.TrendingStoryDao
import com.monoid.hackernews.common.data.room.UserDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object DatabaseDaoBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun providesTopStoryDao(hnDatabase: HNDatabase): TrendingStoryDao = hnDatabase.topStoryDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesNewStoryDao(hnDatabase: HNDatabase): NewStoryDao = hnDatabase.newStoryDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesBestStoryDao(hnDatabase: HNDatabase): HotStoryDao = hnDatabase.bestStoryDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesShowStoryDao(hnDatabase: HNDatabase): ShowStoryDao = hnDatabase.showStoryDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesAskStoryDao(hnDatabase: HNDatabase): AskStoryDao = hnDatabase.askStoryDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesJobStoryDao(hnDatabase: HNDatabase): JobStoryDao = hnDatabase.jobStoryDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesItemDao(hnDatabase: HNDatabase): ItemDao = hnDatabase.itemDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesUserDao(hnDatabase: HNDatabase): UserDao = hnDatabase.userDao()

    @SingleIn(AppScope::class)
    @Provides
    fun providesCommentDao(hnDatabase: HNDatabase): CommentDao = hnDatabase.commentDao()
}
