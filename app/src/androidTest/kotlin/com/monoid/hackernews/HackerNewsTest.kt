package com.monoid.hackernews

import android.app.Activity
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import com.monoid.hackernews.view.main.MainContent
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
}
