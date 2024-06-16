package com.monoid.hackernews.view.stories

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun createStoriesViewModel(key: String): StoriesViewModel {
    return koinViewModel(key = key)
}
