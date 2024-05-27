package com.monoid.hackernews

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.data.FavoriteStoryRepository
import com.monoid.hackernews.common.data.StoriesRepository
import com.monoid.hackernews.common.data.UserStoryRepositoryFactory
import com.monoid.hackernews.view.home.StoriesViewModel
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
        ThemeViewModel(
            preferencesDataSource = get(),
        )
    }

    viewModel {
        StoriesViewModel(
            logger = get(),
            repository = get(),
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
        StoriesRepository(
            logger = get(),
            remoteDataSource = get(),
            topStoryLocalDataSource = get(),
            itemLocalDataSource = get(),
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
        FavoriteStoryRepository(
            preferencesDataSource = get(),
            favoriteLocalDataSource = get(),
        )
    }

    factory {
        Channel<Intent>()
    }
}
