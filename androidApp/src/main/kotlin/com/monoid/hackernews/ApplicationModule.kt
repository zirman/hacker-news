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
import com.monoid.hackernews.view.main.LoginViewModel
import com.monoid.hackernews.view.main.SettingsViewModel
import kotlinx.coroutines.channels.Channel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class LifecycleOwnerQualifier {
    ApplicationLifecycleOwner
}

val applicationModule = module {
    viewModel {
        MainViewModel(
            preferencesDataSource = get(),
            remoteDataSource = get(),
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
            logger = get(),
        )
    }

    viewModel {
        ThemeViewModel(
            preferencesDataSource = get(),
        )
    }

    viewModel {
        LoginViewModel(
            preferencesDataSource = get(),
            remoteDataSource = get(),
            logger = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            preferencesDataSource = get(),
            logger = get(),
        )
    }

    single<LifecycleOwner>(named(LifecycleOwnerQualifier.ApplicationLifecycleOwner)) {
        ProcessLifecycleOwner.get()
    }

    single {
        FavoriteStoryRepository(
            preferencesDataSource = get(),
            favoriteLocalDataSource = get(),
        )
    }

    single {
        UserStoryRepositoryFactory(
            remoteDataSource = get(),
            userLocalDataSource = get(),
            itemLocalDataSource = get(),
        )
    }

    single {
        TopStoryRepository(
            remoteDataSource = get(),
            topStoryLocalDataSource = get(),
        )
    }

    single {
        NewStoryRepository(
            remoteDataSource = get(),
            newStoryLocalDataSource = get(),
        )
    }

    single {
        BestStoryRepository(
            remoteDataSource = get(),
            bestStoryLocalDataSource = get(),
        )
    }

    single {
        AskStoryRepository(
            remoteDataSource = get(),
            askStoryLocalDataSource = get(),
        )
    }

    single {
        ShowStoryRepository(
            httpClient = get(),
            showStoryLocalDataSource = get(),
        )
    }

    single {
        JobStoryRepository(
            remoteDataSource = get(),
            jobStoryLocalDataSource = get(),
        )
    }

    single {
        FavoriteStoryRepository(
            preferencesDataSource = get(),
            favoriteLocalDataSource = get(),
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
