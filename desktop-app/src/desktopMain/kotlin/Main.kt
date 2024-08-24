import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.monoid.hackernews.common.dataStoreModule
import com.monoid.hackernews.common.databaseModule
import com.monoid.hackernews.common.injection.dispatcherModule
import com.monoid.hackernews.common.injection.loggerModule
import com.monoid.hackernews.common.networkModule
import org.koin.core.context.startKoin

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    startKoin {
        modules(
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
