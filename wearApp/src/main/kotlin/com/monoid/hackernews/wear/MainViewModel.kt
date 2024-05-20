package com.monoid.hackernews.wear

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import com.monoid.hackernews.common.injection.LoggerAdapter
import kotlinx.coroutines.channels.Channel

class MainViewModel(
    val newIntentChannel: Channel<Intent>,
    val topStoryRepository: TopStoryRepository,
    val itemTreeRepository: ItemTreeRepository,
    val logger: LoggerAdapter,
) : ViewModel()
