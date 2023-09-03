package com.monoid.hackernews

import android.app.Activity
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import com.monoid.hackernews.view.main.MainContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test

class HackerNewsTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun hackerNews_test() {
        rule.setContent {
            MainContent(
                mainViewModel = hiltViewModel(),
                windowSizeClass = calculateWindowSizeClass(LocalContext.current as Activity)
            )
        }
    }

    @Test
    fun settingMainDispatcher() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
    }
}
