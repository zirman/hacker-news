package com.monoid.hackernews.common.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.view.ViewModelKey
import com.monoid.hackernews.common.view.ViewModelScope
import com.monoid.hackernews.common.data.model.Settings
import com.monoid.hackernews.common.data.model.SettingsRepository
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(NotificationsViewModel::class)
@Inject
class NotificationsViewModel(
    private val repository: SettingsRepository,
    logger: LoggerAdapter,
) : ViewModel() {
    data class UiState(
        val notifications: Boolean = false,
    )

    val uiState = repository.preferences
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = repository.preferences.value.toUiState(),
        )

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    fun onClickNotifications(enabled: Boolean): Job = viewModelScope.launch(context) {
        repository.setNotifications(enabled)
    }
}

private const val TAG = "PreferencesViewModel"

private fun Settings.toUiState(): NotificationsViewModel.UiState = NotificationsViewModel.UiState(
    notifications = notifications,
)
