package com.monoid.hackernews.common.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.model.LoginRepository
import com.monoid.hackernews.common.data.model.Username
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    val logger: LoggerAdapter,
    private val loginRepository: LoginRepository,
) : ViewModel() {
    data class UiState(
        val loading: Boolean = true,
        val username: Username? = null,
    )

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

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

    fun logout(): Job = viewModelScope.launch(context) {
        loginRepository.logout()
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
