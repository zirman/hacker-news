package com.monoid.hackernews

import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.view.ViewModelKey
import com.monoid.hackernews.common.view.ViewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(WearMainViewModel::class)
@Inject
class WearMainViewModel(
//    val newIntentChannel: Channel<Intent>,
    private val logger: LoggerAdapter,
) : ViewModel()
