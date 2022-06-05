package com.monoid.hackernews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.monoid.hackernews.ui.main.MainContent
import com.monoid.hackernews.ui.theme.AppTheme
import com.monoid.hackernews.ui.util.rememberUseDarkTheme
import kotlinx.coroutines.channels.Channel

class MainActivity : FragmentActivity() {
    private lateinit var newIntentChannel: Channel<Intent>

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        newIntentChannel.trySend(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newIntentChannel = Channel()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val useDarkTheme: Boolean =
                rememberUseDarkTheme()

            AppTheme(useDarkTheme = useDarkTheme) {
                val systemUiController: SystemUiController =
                    rememberSystemUiController()

                val primaryColor = MaterialTheme.colorScheme.primary

                SideEffect {
                    systemUiController.setStatusBarColor(
                        // Opaque color is used for window bar on Chrome OS.
                        color = primaryColor.copy(alpha = 0f),
                        darkIcons = useDarkTheme.not(),
                    )

                    systemUiController.setNavigationBarColor(
                        // if alpha is zero, color is ignored
                        color = Color(red = 128, green = 128, blue = 128, alpha = 1),
                        darkIcons = useDarkTheme.not(),
                    )
                }

                MainContent(
                    newIntentChannel = newIntentChannel,
                    windowSizeClass = calculateWindowSizeClass(LocalContext.current as Activity)
                )
            }
        }
    }
}
