package com.monoid.hackernews.common.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.model.LoginRepository
import com.monoid.hackernews.common.data.model.Password
import com.monoid.hackernews.common.data.model.Username
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val logger: LoggerAdapter,
    private val loginRepository: LoginRepository,
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
    val events: Flow<Event> = _events.receiveAsFlow()

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
            loginRepository.preferences.value.username.string.isNotEmpty()
        ) return job
        job = viewModelScope.launch(context) {
            _uiState.update { it.copy(loading = true) }
            try {
                loginRepository.login(username, password)
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
        loginRepository.logout()
    }
}

private const val TAG = "LoginViewModel"
