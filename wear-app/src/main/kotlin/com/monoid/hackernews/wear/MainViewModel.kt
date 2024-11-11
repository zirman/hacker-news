package com.monoid.hackernews.wear

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.injection.LoggerAdapter
import kotlinx.coroutines.channels.Channel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    val newIntentChannel: Channel<Intent>,
    val logger: LoggerAdapter,
) : ViewModel()
