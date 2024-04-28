package com.monoid.hackernews.wear

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.monoid.hackernews.common.data.FavoriteStoryRepository
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import com.monoid.hackernews.common.injection.DispatcherQualifier
import kotlinx.coroutines.channels.Channel
import org.koin.android.ext.koin.androidContext
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
            context = androidContext(),
            authentication = get(),
            favoriteDao = get(),
        )
    }

    viewModel {
        androidContext()
        MainViewModel(
            newIntentChannel = get(),
            topStoryRepository = get(),
            itemTreeRepository = get(),
        )
    }


    single {
        TopStoryRepository(
            httpClient = get(),
            topStoryDao = get(),
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
