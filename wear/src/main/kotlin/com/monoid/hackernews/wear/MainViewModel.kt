package com.monoid.hackernews.wear

import androidx.lifecycle.ViewModel
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val topStoryRepository: TopStoryRepository,
    val itemTreeRepository: ItemTreeRepository,
) : ViewModel()
