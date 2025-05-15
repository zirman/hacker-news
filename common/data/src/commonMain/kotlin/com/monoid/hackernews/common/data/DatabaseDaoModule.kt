package com.monoid.hackernews.common.data

import com.monoid.hackernews.common.data.room.AskStoryDao
import com.monoid.hackernews.common.data.room.HotStoryDao
import com.monoid.hackernews.common.data.room.HNDatabase
import com.monoid.hackernews.common.data.room.ItemDao
import com.monoid.hackernews.common.data.room.JobStoryDao
import com.monoid.hackernews.common.data.room.NewStoryDao
import com.monoid.hackernews.common.data.room.CommentDao
import com.monoid.hackernews.common.data.room.ShowStoryDao
import com.monoid.hackernews.common.data.room.TrendingStoryDao
import com.monoid.hackernews.common.data.room.UserDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabaseDaoModule {

    @Single
    fun topStoryDao(hnDatabase: HNDatabase): TrendingStoryDao = hnDatabase.topStoryDao()

    @Single
    fun newStoryDao(hnDatabase: HNDatabase): NewStoryDao = hnDatabase.newStoryDao()

    @Single
    fun bestStoryDao(hnDatabase: HNDatabase): HotStoryDao = hnDatabase.bestStoryDao()

    @Single
    fun showStoryDao(hnDatabase: HNDatabase): ShowStoryDao = hnDatabase.showStoryDao()

    @Single
    fun askStoryDao(hnDatabase: HNDatabase): AskStoryDao = hnDatabase.askStoryDao()

    @Single
    fun jobStoryDao(hnDatabase: HNDatabase): JobStoryDao = hnDatabase.jobStoryDao()

    @Single
    fun itemDao(hnDatabase: HNDatabase): ItemDao = hnDatabase.itemDao()

    @Single
    fun userDao(hnDatabase: HNDatabase): UserDao = hnDatabase.userDao()

    @Single
    fun commentDao(hnDatabase: HNDatabase): CommentDao = hnDatabase.commentDao()
}
