package com.monoid.hackernews

import androidx.lifecycle.ViewModel
import com.monoid.hackernews.repo.ItemRepo
import com.monoid.hackernews.room.AskStoryDao
import com.monoid.hackernews.room.BestStoryDao
import com.monoid.hackernews.room.ExpandedDao
import com.monoid.hackernews.room.FavoriteDao
import com.monoid.hackernews.room.HNDatabase
import com.monoid.hackernews.room.ItemDao
import com.monoid.hackernews.room.JobStoryDao
import com.monoid.hackernews.room.NewStoryDao
import com.monoid.hackernews.room.ShowStoryDao
import com.monoid.hackernews.room.TopStoryDao
import com.monoid.hackernews.room.UpvoteDao
import com.monoid.hackernews.room.UserDao
import io.ktor.client.HttpClient

class MainViewModel : ViewModel() {
    val httpClient: HttpClient
    val topStoryDao: TopStoryDao
    val newStoryDao: NewStoryDao
    val bestStoryDao: BestStoryDao
    val showStoryDao: ShowStoryDao
    val askStoryDao: AskStoryDao
    val jobStoryDao: JobStoryDao
    val itemDao: ItemDao
    val userDao: UserDao
    val upvoteDao: UpvoteDao
    val favoriteDao: FavoriteDao
    val expandedDao: ExpandedDao
    val itemRepo: ItemRepo

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
        expandedDao = db.expandedDao()

        itemRepo = ItemRepo(
            authenticationDataStore = app.settingsDataStore,
            httpClient = httpClient,
            itemDao = itemDao,
            upvoteDao = upvoteDao,
            favoriteDao = favoriteDao,
            expandedDao = expandedDao,
        )
    }
}
