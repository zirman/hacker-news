package com.monoid.hackernews.wear

import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
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

    viewModel {
        MainViewModel(
            newIntentChannel = get(),
            topStoryRepository = get(),
            itemTreeRepository = get(),
            logger = get(),
        )
    }

    factory {
        Channel<Intent>()
    }
}
