@file:OptIn(ExperimentalSplitPaneApi::class)

package com.monoid.hackernews

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.monoid.hackernews.common.core.metro.LocalViewModelProviderFactory
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.hacker_news
import dev.zacsweers.metro.createGraph
import io.ktor.http.toURI
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import java.awt.Desktop
import kotlin.reflect.KClass

fun main() {
    val appGraph = createGraph<DesktopAppGraph>()
    application {
        CompositionLocalProvider(LocalViewModelProviderFactory provides object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                return appGraph.desktopViewModelFactory.create(modelClass, extras)
            }
        }) {
            Window(
                onCloseRequest = ::exitApplication,
                alwaysOnTop = false,
                title = stringResource(Res.string.hacker_news),
            ) {
                DesktopApp(
                    onClickUrl = { url ->
                        try {
                            (if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null)
                                ?.takeIf { it.isSupported(Desktop.Action.BROWSE) }
                                ?.run {
                                    browse(url.toURI())
                                    true
                                }
                                ?: false
                        } catch (throwable: Throwable) {
                            throwable.printStackTrace()
                        }
                    },
                )
            }
        }
    }
}
