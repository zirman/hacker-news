package com.monoid.hackernews.common.view.main

import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.data.model.SettingsRepository
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val isLoggedIn get() = settingsRepository.isLoggedIn
}
