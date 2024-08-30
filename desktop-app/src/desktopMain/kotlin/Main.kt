import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.monoid.hackernews.applicationModule
import com.monoid.hackernews.common.dataStoreModule
import com.monoid.hackernews.common.databaseModule
import com.monoid.hackernews.common.injection.dispatcherModule
import com.monoid.hackernews.common.injection.loggerModule
import com.monoid.hackernews.common.networkModule
import com.monoid.hackernews.view.main.MainNavHost
import com.monoid.hackernews.view.theme.AppTheme
import org.koin.core.context.startKoin

@Composable
fun App() {
    AppTheme {
        MainNavHost()
    }
}

fun main() = application {
    startKoin {
        modules(
            applicationModule,
            dispatcherModule,
            networkModule,
            databaseModule,
            dataStoreModule,
            loggerModule,
        )
    }

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
