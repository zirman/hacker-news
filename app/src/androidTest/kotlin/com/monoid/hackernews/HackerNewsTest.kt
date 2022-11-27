package com.monoid.hackernews

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.test.junit4.createComposeRule
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
                windowSizeClass = WindowSizeClass(
                    widthSizeClass = WindowWidthSizeClass.Compact,
                    heightSizeClass = WindowHeightSizeClass.Compact
                )
            )
        }
    }
}
