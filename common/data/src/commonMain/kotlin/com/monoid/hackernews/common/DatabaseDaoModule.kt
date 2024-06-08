package com.monoid.hackernews.common

import com.monoid.hackernews.common.room.HNDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

val databaseDaoModule: Module = module {
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
}
