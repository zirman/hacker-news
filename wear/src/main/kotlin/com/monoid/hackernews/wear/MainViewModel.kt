package com.monoid.hackernews.wear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monoid.hackernews.common.data.ItemTreeRepository
import com.monoid.hackernews.common.data.TopStoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val topStoryRepository: TopStoryRepository,
    val itemTreeRepository: ItemTreeRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            itemTreeRepository.cleanupJob()
        }
    }
}
