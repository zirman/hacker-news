package com.monoid.hackernews.common.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.coroutines.mapStateIn
import com.monoid.hackernews.common.data.model.LoginRepository
import com.monoid.hackernews.common.data.model.Username
import kotlinx.coroutines.flow.SharingStarted
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    loginRepository: LoginRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean,
        val username: Username,
    )

    val uiState = loginRepository.preferences
        .mapStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        ) { preferences ->
            UiState(
                loading = false,
                username = preferences.username,
            )
        }
}
