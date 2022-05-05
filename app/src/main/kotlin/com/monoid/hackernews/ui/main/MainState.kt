package com.monoid.hackernews.ui.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.monoid.hackernews.HNApplication
import com.monoid.hackernews.repo.ItemRepo
import com.monoid.hackernews.room.AskStoryDao
import com.monoid.hackernews.room.BestStoryDao
import com.monoid.hackernews.room.ExpandedDao
import com.monoid.hackernews.room.FavoriteDao
import com.monoid.hackernews.room.ItemDao
import com.monoid.hackernews.room.JobStoryDao
import com.monoid.hackernews.room.NewStoryDao
import com.monoid.hackernews.room.ShowStoryDao
import com.monoid.hackernews.room.TopStoryDao
import com.monoid.hackernews.room.UpvoteDao
import com.monoid.hackernews.room.UserDao
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Composable
fun rememberMainState(): MainState {
    val client =
        remember {
            HttpClient(Android) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.ALL
                }

                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
            }
        }

    DisposableEffect(Unit) { onDispose { client.close() } }

    val drawerState: DrawerState =
        rememberDrawerState(DrawerValue.Closed)

    val db = HNApplication.instance.db

    val itemDao =
        db.itemDao()

    val expandedDao =
        db.expandedDao()

    return remember {
        MainState(
            httpClient = client,
            topStoryDao = db.topStoryDao(),
            newStoryDao = db.newStoryDao(),
            bestStoryDao = db.bestStoryDao(),
            showStoryDao = db.showStoryDao(),
            askStoryDao = db.askStoryDao(),
            jobStoryDao = db.jobStoryDao(),
            itemDao = itemDao,
            userDao = db.userDao(),
            upvoteDao = db.upvoteDao(),
            favoriteDao = db.favoriteDao(),
            itemRepo = ItemRepo(
                httpClient = client,
                itemDao = itemDao,
                expandedDao = expandedDao,
            ),
            drawerState = drawerState,
        )
    }
}

@Stable
class MainState(
    val httpClient: HttpClient,
    val topStoryDao: TopStoryDao,
    val newStoryDao: NewStoryDao,
    val bestStoryDao: BestStoryDao,
    val showStoryDao: ShowStoryDao,
    val askStoryDao: AskStoryDao,
    val jobStoryDao: JobStoryDao,
    val itemDao: ItemDao,
    val userDao: UserDao,
    val upvoteDao: UpvoteDao,
    val favoriteDao: FavoriteDao,
    val itemRepo: ItemRepo,
    val drawerState: DrawerState,
)
