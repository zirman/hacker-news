package com.monoid.hackernews.common

import android.content.Context
import androidx.room.Room
import com.monoid.hackernews.common.room.AskStoryDao
import com.monoid.hackernews.common.room.BestStoryDao
import com.monoid.hackernews.common.room.ExpandedDao
import com.monoid.hackernews.common.room.FavoriteDao
import com.monoid.hackernews.common.room.FlagDao
import com.monoid.hackernews.common.room.HNDatabase
import com.monoid.hackernews.common.room.ItemDao
import com.monoid.hackernews.common.room.JobStoryDao
import com.monoid.hackernews.common.room.NewStoryDao
import com.monoid.hackernews.common.room.ShowStoryDao
import com.monoid.hackernews.common.room.TopStoryDao
import com.monoid.hackernews.common.room.UpvoteDao
import com.monoid.hackernews.common.room.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideHNDatabase(@ApplicationContext applicationContext: Context): HNDatabase {
        return Room
            .databaseBuilder(
                applicationContext,
                HNDatabase::class.java,
                "hacker-news-database"
            )
            .build()
    }

    @Provides
    fun provideTopStoryDao(hnDatabase: HNDatabase): TopStoryDao {
        return hnDatabase.topStoryDao()
    }

    @Provides
    fun provideNewStoryDao(hnDatabase: HNDatabase): NewStoryDao {
        return hnDatabase.newStoryDao()
    }

    @Provides
    fun provideBestStoryDao(hnDatabase: HNDatabase): BestStoryDao {
        return hnDatabase.bestStoryDao()
    }

    @Provides
    fun provideShowStoryDao(hnDatabase: HNDatabase): ShowStoryDao {
        return hnDatabase.showStoryDao()
    }

    @Provides
    fun provideAskStoryDao(hnDatabase: HNDatabase): AskStoryDao {
        return hnDatabase.askStoryDao()
    }

    @Provides
    fun provideJobStoryDao(hnDatabase: HNDatabase): JobStoryDao {
        return hnDatabase.jobStoryDao()
    }

    @Provides
    fun provideItemDao(hnDatabase: HNDatabase): ItemDao {
        return hnDatabase.itemDao()
    }

    @Provides
    fun provideUserDao(hnDatabase: HNDatabase): UserDao {
        return hnDatabase.userDao()
    }

    @Provides
    fun provideUpvoteDao(hnDatabase: HNDatabase): UpvoteDao {
        return hnDatabase.upvoteDao()
    }

    @Provides
    fun provideFavoriteDao(hnDatabase: HNDatabase): FavoriteDao {
        return hnDatabase.favoriteDao()
    }

    @Provides
    fun provideFlagDao(hnDatabase: HNDatabase): FlagDao {
        return hnDatabase.flagDao()
    }

    @Provides
    fun provideExpandedDao(hnDatabase: HNDatabase): ExpandedDao {
        return hnDatabase.expandedDao()
    }
}
