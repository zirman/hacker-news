package com.monoid.hackernews.common.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.model.LoginRepository
import com.monoid.hackernews.common.data.model.Username
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    val logger: LoggerAdapter,
    loginRepository: LoginRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = true,
        val username: Username? = null,
    )

    val uiState = loginRepository.preferences
        .map { preferences ->
            UiState(
                loading = false,
                username = if (preferences.password.string.isNotEmpty()) {
                    preferences.username
                } else {
                    null
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = UiState(),
        )
}
