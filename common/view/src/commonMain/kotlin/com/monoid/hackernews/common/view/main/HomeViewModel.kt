package com.monoid.hackernews.common.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.view.ViewModelKey
import com.monoid.hackernews.common.view.ViewModelScope
import com.monoid.hackernews.common.data.api.ItemId
import com.monoid.hackernews.common.data.model.Item
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.Username
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(HomeViewModel::class)
@Inject
class HomeViewModel(
    private val logger: LoggerAdapter,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    sealed interface Event {
        data class OpenReply(val itemId: ItemId) : Event
        data object OpenLogin : Event
        data class OpenUser(val username: Username) : Event
        data class OpenStory(val itemId: ItemId) : Event
    }

    private val _events = Channel<Event>()
    val events: ReceiveChannel<Event> = _events

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException("CoroutineExceptionHandler", throwable, TAG)
    }

    fun onClickUser(username: Username): Job = viewModelScope.launch(context) {
        _events.send(Event.OpenUser(username))
    }

    fun onClickReply(itemId: ItemId): Job = viewModelScope.launch(context) {
        if (settingsRepository.isLoggedIn) {
            _events.send(Event.OpenReply(itemId))
        } else {
            _events.send(Event.OpenLogin)
        }
    }

    fun onClickStory(item: Item): Job = viewModelScope.launch(context) {
        _events.send(Event.OpenStory(item.id))
    }
}

private const val TAG = "HomeViewModel"
