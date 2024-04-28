package com.monoid.hackernews.common

import androidx.room.Room
import com.monoid.hackernews.common.room.HNDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room
            .databaseBuilder(
                context = androidContext(),
                klass = HNDatabase::class.java,
                name = "hacker-news-database",
            )
            .build()
    }

    single {
        get<HNDatabase>().topStoryDao()
    }

    single {
        get<HNDatabase>().newStoryDao()
    }

    single {
        get<HNDatabase>().bestStoryDao()
    }

    single {
        get<HNDatabase>().showStoryDao()
    }

    single {
        get<HNDatabase>().askStoryDao()
    }

    single {
        get<HNDatabase>().jobStoryDao()
    }

    single {
        get<HNDatabase>().itemDao()
    }

    single {
        get<HNDatabase>().userDao()
    }

    single {
        get<HNDatabase>().upvoteDao()
    }

    single {
        get<HNDatabase>().favoriteDao()
    }

    single {
        get<HNDatabase>().flagDao()
    }

    single {
        get<HNDatabase>().expandedDao()
    }

    single {
        get<HNDatabase>().followedDao()
    }
}
