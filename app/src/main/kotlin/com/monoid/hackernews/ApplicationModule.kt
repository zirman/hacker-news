package com.monoid.hackernews

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.data.AskStoryRepository
import com.monoid.hackernews.common.data.BestStoryRepository
import com.monoid.hackernews.common.data.FavoriteStoryRepository
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.JobStoryRepository
import com.monoid.hackernews.common.data.NewStoryRepository
import com.monoid.hackernews.common.data.ShowStoryRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import com.monoid.hackernews.common.data.UserStoryRepositoryFactory
import com.monoid.hackernews.common.injection.DispatcherQualifier
import kotlinx.coroutines.channels.Channel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class LifecycleOwnerQualifier {
    ApplicationLifecycleOwner
}

val applicationModule = module {
    viewModel {
        MainViewModel(
            authentication = get(),
            httpClient = get(),
            userStoryRepositoryFactory = get(),
            topStoryRepository = get(),
            newStoryRepository = get(),
            bestStoryRepository = get(),
            askStoryRepository = get(),
            showStoryRepository = get(),
            jobStoryRepository = get(),
            favoriteStoryRepository = get(),
            itemTreeRepository = get(),
            newIntentChannel = get(),
        )
    }

    single<LifecycleOwner>(named(LifecycleOwnerQualifier.ApplicationLifecycleOwner)) {
        ProcessLifecycleOwner.get()
    }

    single {
        FavoriteStoryRepository(
            context = androidContext(),
            authentication = get(),
            favoriteDao = get(),
        )
    }

    single {
        UserStoryRepositoryFactory(
            httpClient = get(),
            userDao = get(),
            itemDao = get(),
        )
    }

    single {
        TopStoryRepository(
            httpClient = get(),
            topStoryDao = get(),
        )
    }

    single {
        NewStoryRepository(
            httpClient = get(),
            newStoryDao = get(),
        )
    }

    single {
        BestStoryRepository(
            httpClient = get(),
            bestStoryDao = get(),
        )
    }

    single {
        AskStoryRepository(
            httpClient = get(),
            askStoryDao = get(),
        )
    }

    single {
        ShowStoryRepository(
            httpClient = get(),
            showStoryDao = get(),
        )
    }

    single {
        JobStoryRepository(
            httpClient = get(),
            jobStoryDao = get(),
        )
    }

    single {
        FavoriteStoryRepository(
            context = get(),
            authentication = get(),
            favoriteDao = get(),
        )
    }

    single {
        ItemTreeRepository(
            authentication = get(),
            httpClient = get(),
            firebaseCrashlytics = get(),
            itemDao = get(),
            upvoteDao = get(),
            favoriteDao = get(),
            flagDao = get(),
            expandedDao = get(),
            followedDao = get(),
            mainDispatcher = get(),
            ioDispatcher = get(named(DispatcherQualifier.Io)),
        )
    }

    factory {
        Channel<Intent>()
    }
}
