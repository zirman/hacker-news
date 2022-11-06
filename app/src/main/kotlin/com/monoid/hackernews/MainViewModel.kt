package com.monoid.hackernews

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.shared.data.ItemTreeRepository
import com.monoid.hackernews.shared.data.settingsDataStore
import com.monoid.hackernews.shared.room.AskStoryDao
import com.monoid.hackernews.shared.room.BestStoryDao
import com.monoid.hackernews.shared.room.ExpandedDao
import com.monoid.hackernews.shared.room.FavoriteDao
import com.monoid.hackernews.shared.room.FlagDao
import com.monoid.hackernews.shared.room.HNDatabase
import com.monoid.hackernews.shared.room.ItemDao
import com.monoid.hackernews.shared.room.JobStoryDao
import com.monoid.hackernews.shared.room.NewStoryDao
import com.monoid.hackernews.shared.room.ShowStoryDao
import com.monoid.hackernews.shared.room.TopStoryDao
import com.monoid.hackernews.shared.room.UpvoteDao
import com.monoid.hackernews.shared.room.UserDao
import io.ktor.client.HttpClient
import kotlinx.coroutines.channels.Channel

class MainViewModel : ViewModel() {
    val newIntentChannel = Channel<Intent>()
    val httpClient: HttpClient
    val topStoryDao: TopStoryDao
    val newStoryDao: NewStoryDao
    val bestStoryDao: BestStoryDao
    val showStoryDao: ShowStoryDao
    val askStoryDao: AskStoryDao
    val jobStoryDao: JobStoryDao
    val itemDao: ItemDao
    val userDao: UserDao
    private val upvoteDao: UpvoteDao
    val favoriteDao: FavoriteDao
    private val flagDao: FlagDao
    private val expandedDao: ExpandedDao
    val itemTreeRepository: ItemTreeRepository

    init {
        val app: HNApplication =
            HNApplication.instance

        val db: HNDatabase =
            app.db

        httpClient =
            app.httpClient

        topStoryDao = db.topStoryDao()
        newStoryDao = db.newStoryDao()
        bestStoryDao = db.bestStoryDao()
        showStoryDao = db.showStoryDao()
        askStoryDao = db.askStoryDao()
        jobStoryDao = db.jobStoryDao()
        itemDao = db.itemDao()
        userDao = db.userDao()
        upvoteDao = app.upvoteDao
        favoriteDao = app.favoriteDao
        flagDao = app.flagDao
        expandedDao = db.expandedDao()

        itemTreeRepository = ItemTreeRepository(
            coroutineScope = viewModelScope,
            authenticationDataStore = app.settingsDataStore,
            httpClient = httpClient,
            itemDao = itemDao,
            upvoteDao = upvoteDao,
            favoriteDao = favoriteDao,
            flagDao = flagDao,
            expandedDao = expandedDao,
        )
    }
}
