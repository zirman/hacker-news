package com.monoid.hackernews.wear

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.data.FavoriteStoryRepository
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import com.monoid.hackernews.common.injection.DispatcherQualifier
import kotlinx.coroutines.channels.Channel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class LifecycleOwnerQualifier {
    ApplicationLifecycleOwner
}

val wearApplicationModule = module {
    single<LifecycleOwner>(named(LifecycleOwnerQualifier.ApplicationLifecycleOwner)) {
        ProcessLifecycleOwner.get()
    }

    single {
        FavoriteStoryRepository(
            preferencesDataSource = get(),
            favoriteLocalDataSource = get(),
        )
    }

    viewModel {
        MainViewModel(
            newIntentChannel = get(),
            topStoryRepository = get(),
            itemTreeRepository = get(),
            logger = get(),
        )
    }

    single {
        TopStoryRepository(
            remoteDataSource = get(),
            topStoryLocalDataSource = get(),
        )
    }

    single {
        ItemTreeRepository(
            preferences = get(),
            remoteDataSource = get(),
            logger = get(),
            itemLocalDataSource = get(),
            upvoteLocalDataSource = get(),
            favoriteLocalDataSource = get(),
            flagLocalDataSource = get(),
            expandedLocalDataSource = get(),
            followedLocalDataSource = get(),
            mainDispatcher = get(),
            ioDispatcher = get(named(DispatcherQualifier.Io)),
        )
    }

    factory {
        Channel<Intent>()
    }
}
