package com.monoid.hackernews

import android.content.Intent
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.api.ItemId
import com.monoid.hackernews.common.data.AskStoryRepository
import com.monoid.hackernews.common.data.BestStoryRepository
import com.monoid.hackernews.common.data.FavoriteStoryRepository
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.JobStoryRepository
import com.monoid.hackernews.common.data.LoginAction
import com.monoid.hackernews.common.data.NewStoryRepository
import com.monoid.hackernews.common.data.Preferences
import com.monoid.hackernews.common.data.ShowStoryRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import com.monoid.hackernews.common.data.UserStoryRepositoryFactory
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.injection.LoggerAdapter
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    val preferencesDataSource: DataStore<Preferences>,
    val remoteDataSource: HttpClient,
    val userStoryRepositoryFactory: UserStoryRepositoryFactory,
    val topStoryRepository: TopStoryRepository,
    val newStoryRepository: NewStoryRepository,
    val bestStoryRepository: BestStoryRepository,
    val askStoryRepository: AskStoryRepository,
    val showStoryRepository: ShowStoryRepository,
    val jobStoryRepository: JobStoryRepository,
    val favoriteStoryRepository: FavoriteStoryRepository,
    val itemTreeRepository: ItemTreeRepository,
    val newIntentChannel: Channel<Intent>,
    val logger: LoggerAdapter,
) : ViewModel() {
    sealed interface Event {
        data class NavigateToReply(val itemId: ItemId) : Event
        data class NavigateToLogin(val loginAction: LoginAction) : Event
        data class NavigateToUser(val user: Username) : Event
        data class NavigateToBrowser(val uri: Uri) : Event
        data object UrlIsNull : Event
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

    fun clickReply(itemId: ItemId): Job = viewModelScope.launch(context) {
        val auth = preferencesDataSource.data.first()

        if (auth.password.string.isNotEmpty()) {
            _events.send(Event.NavigateToReply(itemId))
        } else {
            _events.send(Event.NavigateToLogin(LoginAction.Reply(itemId.long)))
        }
    }

    fun clickUser(user: Username?): Job = viewModelScope.launch(context) {
        if (user != null) {
            _events.send(Event.NavigateToUser(user))
        } else {
            _events.send(Event.UrlIsNull)
        }
    }

    fun clickBrowser(url: String?): Job = viewModelScope.launch(context) {
        val uri = url?.let { Uri.parse(it) }

        if (uri != null) {
            _events.send(Event.NavigateToBrowser(uri))
        } else {
            _events.send(Event.UrlIsNull)
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
