package com.monoid.hackernews

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.data.LoginRepository
import com.monoid.hackernews.common.data.PreferencesRepository
import com.monoid.hackernews.common.data.StoriesRepository
import com.monoid.hackernews.common.data.UserStoryRepositoryFactory
import com.monoid.hackernews.common.injection.DispatcherQualifier
import com.monoid.hackernews.view.itemdetail.ItemDetailViewModel
import com.monoid.hackernews.view.main.LoginViewModel
import com.monoid.hackernews.view.settings.PreferencesViewModel
import com.monoid.hackernews.view.settings.SettingsViewModel
import com.monoid.hackernews.view.stories.StoriesViewModel
import kotlinx.coroutines.channels.Channel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class LifecycleOwnerQualifier {
    ApplicationLifecycleOwner
}

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
        LoginRepository(
            logger = get(),
            remoteDataSource = get(),
            localDataSource = get(),
        )
    }

    single {
        PreferencesRepository(
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

    factory {
        Channel<Intent>()
    }
}
