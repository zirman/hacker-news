package com.monoid.hackernews.common.view.stories

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun createStoriesViewModel(key: String): StoriesViewModel {
    return koinViewModel(key = key)
}
