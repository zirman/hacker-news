package com.monoid.hackernews

import com.monoid.hackernews.common.data.model.LoginRepository
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.StoriesRepository
import com.monoid.hackernews.common.data.model.UserStoryRepositoryFactory
import com.monoid.hackernews.common.injection.DispatcherQualifier
import com.monoid.hackernews.view.itemdetail.ItemDetailViewModel
import com.monoid.hackernews.view.main.LoginViewModel
import com.monoid.hackernews.view.settings.PreferencesViewModel
import com.monoid.hackernews.view.settings.SettingsViewModel
import com.monoid.hackernews.view.stories.StoriesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationModule = module {
    viewModel {
        StoriesViewModel(
            logger = get(),
            repository = get(),
        )
    }
    viewModel {
        ItemDetailViewModel(
            savedStateHandle = get(),
            defaultDispatcher = get(named(DispatcherQualifier.Default)),
            logger = get(),
            repository = get(),
        )
    }
    viewModel {
        SettingsViewModel(
            logger = get(),
            loginRepository = get(),
        )
    }
    viewModel {
        LoginViewModel(
            logger = get(),
            loginRepository = get(),
        )
    }
    viewModel {
        PreferencesViewModel(
            repository = get(),
            logger = get(),
        )
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
        LoginRepository(
            logger = get(),
            remoteDataSource = get(),
            localDataSource = get(),
        )
    }
    single {
        SettingsRepository(
            logger = get(),
            localDataSource = get(),
        )
    }
    single {
        UserStoryRepositoryFactory(
            remoteDataSource = get(),
            userLocalDataSource = get(),
            itemLocalDataSource = get(),
        )
    }
}
