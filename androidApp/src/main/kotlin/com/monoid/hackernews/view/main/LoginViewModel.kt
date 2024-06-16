package com.monoid.hackernews.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.data.LoginRepository
import com.monoid.hackernews.common.data.Password
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.injection.LoggerAdapter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    logger: LoggerAdapter,
    private val loginRepository: LoginRepository,
) : ViewModel() {
    sealed interface Event {
        data object DismissRequest : Event
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
    ): Job = viewModelScope.launch(context) {
        loginRepository.login(username, password)
        _events.send(Event.DismissRequest)
//                is LoginAction.Upvote -> {
//                    scope.launch {
//                        itemTreeRepository.upvoteItemJob(
//                            authentication,
//                            ItemId(showBottomSheet.itemId)
//                        )
//                    }
//                }
//                is LoginAction.Favorite -> {
//                    scope.launch {
//                        itemTreeRepository.favoriteItemJob(
//                            authentication,
//                            ItemId(showBottomSheet.itemId)
//                        )
//                    }
//                }
//                is LoginAction.Flag -> {
//                    scope.launch {
//                        itemTreeRepository.flagItemJob(
//                            authentication,
//                            ItemId(showBottomSheet.itemId)
//                        )
//                    }
//                }
//                is LoginAction.Reply -> {
//                    onNavigateToReply(ItemId(showBottomSheet.itemId))
//                }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
