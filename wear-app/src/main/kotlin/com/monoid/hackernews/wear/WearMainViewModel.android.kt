package com.monoid.hackernews.wear

import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ViewModelKey
import com.monoid.hackernews.common.core.metro.ViewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(WearMainViewModel::class)
@Inject
class WearMainViewModel(
//    val newIntentChannel: Channel<Intent>,
    private val logger: LoggerAdapter,
) : ViewModel()
