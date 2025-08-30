@file:OptIn(ExperimentalSplitPaneApi::class)

package com.monoid.hackernews

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.monoid.hackernews.common.view.App
import com.monoid.hackernews.common.view.ApplicationModule
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.hacker_news
import io.ktor.http.toURI
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import java.awt.Desktop

fun main() {
    startKoin {
        modules(ApplicationModule.module)
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = false,
            title = stringResource(Res.string.hacker_news),
        ) {
            App(
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
