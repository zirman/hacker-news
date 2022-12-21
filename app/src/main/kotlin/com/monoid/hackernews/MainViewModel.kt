package com.monoid.hackernews

import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.data.AskStoryRepository
import com.monoid.hackernews.common.data.BestStoryRepository
import com.monoid.hackernews.common.data.FavoriteStoryRepository
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.JobStoryRepository
import com.monoid.hackernews.common.data.NewStoryRepository
import com.monoid.hackernews.common.data.ShowStoryRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import com.monoid.hackernews.common.data.UserStoryRepository
import com.monoid.hackernews.common.datastore.Authentication
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val authentication: DataStore<Authentication>,
    val httpClient: HttpClient,
    val userStoryRepository: UserStoryRepository,
    val topStoryRepository: TopStoryRepository,
    val newStoryRepository: NewStoryRepository,
    val bestStoryRepository: BestStoryRepository,
    val askStoryRepository: AskStoryRepository,
    val showStoryRepository: ShowStoryRepository,
    val jobStoryRepository: JobStoryRepository,
    val favoriteStoryRepository: FavoriteStoryRepository,
    val itemTreeRepository: ItemTreeRepository,
    val newIntentChannel: Channel<Intent>,
) : ViewModel()
