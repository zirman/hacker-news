package com.monoid.hackernews.common.view.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.log.LoggerAdapter
import com.monoid.hackernews.common.core.metro.ViewModelKey
import com.monoid.hackernews.common.core.metro.ViewModelScope
import com.monoid.hackernews.common.data.model.Password
import com.monoid.hackernews.common.data.model.SettingsRepository
import com.monoid.hackernews.common.data.model.Username
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(LoginViewModel::class)
@Inject
class LoginViewModel(
    private val logger: LoggerAdapter,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    data class UiState(
        val showDialog: Boolean = false,
        val loading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private var job: Job? = null

    sealed interface Event {
        data object DismissRequest : Event
        data object Error : Event
    }

    private val _events: Channel<Event> = Channel()
    val events: ReceiveChannel<Event> = _events

    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    fun onSubmit(
        username: Username,
        password: Password,
    ): Job? {
        if (job?.isActive == true ||
            settingsRepository.preferences.value.username.string.isNotEmpty()
        ) {
            return job
        }
        job = viewModelScope.launch(context) {
            _uiState.update { it.copy(loading = true) }
            try {
                settingsRepository.login(username, password)
                _events.send(Event.DismissRequest)
            } catch (throwable: Throwable) {
                currentCoroutineContext().ensureActive()
                _events.send(Event.Error)
                throw throwable
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }
        return job
    }

    fun logout(): Job = viewModelScope.launch(context) {
        settingsRepository.logout()
    }
}

private const val TAG = "LoginViewModel"
