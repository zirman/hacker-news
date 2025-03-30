package com.monoid.hackernews.common.view.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.core.LoggerAdapter
import com.monoid.hackernews.common.data.model.LoginRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LogoutViewModel(
    private val logger: LoggerAdapter,
    private val loginRepository: LoginRepository,
) : ViewModel() {
    private val context = CoroutineExceptionHandler { _, throwable ->
        logger.recordException(
            messageString = "CoroutineExceptionHandler",
            throwable = throwable,
            tag = TAG,
        )
    }

    fun logout(): Job = viewModelScope.launch(context) {
        loginRepository.logout()
    }
}

private const val TAG = "LoginViewModel"
