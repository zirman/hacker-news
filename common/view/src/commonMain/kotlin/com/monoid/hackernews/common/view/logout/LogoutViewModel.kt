package com.monoid.hackernews.common.view.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.view.ViewModelKey
import com.monoid.hackernews.common.view.ViewModelScope
import com.monoid.hackernews.common.data.model.SettingsRepository
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ContributesIntoMap(ViewModelScope::class)
@ViewModelKey(LogoutViewModel::class)
@Inject
class LogoutViewModel(
    private val logger: LoggerAdapter,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    fun logout(): Job = viewModelScope.launch(context) {
        settingsRepository.logout()
    }
}

private const val TAG = "LoginViewModel"
