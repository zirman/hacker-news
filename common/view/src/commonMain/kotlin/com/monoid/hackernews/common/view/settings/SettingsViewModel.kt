package com.monoid.hackernews.common.view.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.view.ViewModelKey
import com.monoid.hackernews.common.view.ViewModelScope
import com.monoid.hackernews.common.core.coroutines.mapStateIn
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.Username
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(SettingsViewModel::class)
@Inject
class SettingsViewModel(
    logger: LoggerAdapter,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    sealed interface Event {
        data class OpenReply(val itemId: ItemId) : Event
        data object OpenLogin : Event
    }

    data class UiState(
        val username: Username = Username(""),
    )

    private val _events = Channel<Event>()
    val events: ReceiveChannel<Event> = _events

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException("CoroutineExceptionHandler", throwable, TAG)
    }

    val uiState = settingsRepository.preferences
        .mapStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        ) { preferences ->
            UiState(
                username = preferences.username,
            )
        }

    fun onClickReply(itemId: ItemId): Job = viewModelScope.launch(context) {
        if (uiState.value.username.string.isNotEmpty()) {
            _events.send(Event.OpenReply(itemId))
        } else {
            _events.send(Event.OpenLogin)
        }
    }
}

private const val TAG = "SettingsViewModel"
