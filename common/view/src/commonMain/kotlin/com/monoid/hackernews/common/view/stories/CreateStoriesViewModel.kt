package com.monoid.hackernews.common.view.stories

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun createStoriesViewModel(storyOrdering: StoryOrdering): StoriesViewModel = koinViewModel(
    key = storyOrdering.toString(),
    parameters = { parametersOf(storyOrdering) },
)
