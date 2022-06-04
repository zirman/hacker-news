package com.monoid.hackernews.ui.login

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monoid.hackernews.MainViewModel
import com.monoid.hackernews.R
import com.monoid.hackernews.api.loginRequest
import com.monoid.hackernews.datastore.Authentication
import com.monoid.hackernews.datastore.authentication
import com.monoid.hackernews.datastore.copy
import com.monoid.hackernews.settingsDataStore
import com.monoid.hackernews.ui.text.PasswordTextField
import com.monoid.hackernews.ui.text.UsernameTextField
import com.monoid.hackernews.ui.util.WindowSize
import com.monoid.hackernews.ui.util.WindowSizeClass
import io.ktor.client.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun LoginContent(
    windowSizeState: State<WindowSize>,
    onLogin: () -> Unit,
    onLoginError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mainViewModel: MainViewModel = viewModel()

    val windowSize = windowSizeState.value

    Surface(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.tertiary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    if (windowSize.height == WindowSizeClass.Compact) {
                        WindowInsets.safeContent.only(WindowInsetsSides.Vertical)
                    } else {
                        WindowInsets.safeContent.only(WindowInsetsSides.Bottom)
                    }.asPaddingValues()
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val rowModifier: Modifier =
                if (windowSize.width == WindowSizeClass.Compact) {
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                } else {
                    Modifier.width(320.dp)
                }.padding(vertical = 4.dp)

            val coroutineScope: CoroutineScope =
                rememberCoroutineScope()

            val (username: String, setUsername) =
                rememberSaveable { mutableStateOf("") }

            val (password: String, setPassword) =
                rememberSaveable { mutableStateOf("") }

            val focusManager: FocusManager =
                LocalFocusManager.current

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.hacker_news_login),
                modifier = rowModifier,
                style = MaterialTheme.typography.headlineMedium,
            )

            UsernameTextField(
                username = username,
                onUsernameChange = setUsername,
                modifier = rowModifier,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            )

            val context: Context =
                LocalContext.current

            PasswordTextField(
                password = password,
                onChangePassword = setPassword,
                modifier = rowModifier,
                onDone = {
                    submitJob(
                        context = context,
                        coroutineScope = coroutineScope,
                        httpClient = mainViewModel.httpClient,
                        authentication = authentication {
                            this.username = username
                            this.password = password
                        },
                        onLogin = onLogin,
                        onLoginError = onLoginError,
                    )
                },
            )

            Button(
                onClick = {
                    submitJob(
                        context = context,
                        coroutineScope = coroutineScope,
                        httpClient = mainViewModel.httpClient,
                        authentication = authentication {
                            this.username = username
                            this.password = password
                        },
                        onLogin = onLogin,
                        onLoginError = onLoginError,
                    )
                },
                modifier = rowModifier,
                enabled = username.isNotBlank() && password.isNotBlank(),
            ) {
                Text(text = stringResource(id = R.string.submit))
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

fun submitJob(
    context: Context,
    coroutineScope: CoroutineScope,
    httpClient: HttpClient,
    authentication: Authentication,
    onLogin: () -> Unit,
    onLoginError: (Throwable) -> Unit,
): Job {
    return coroutineScope.launch {
        try {
            httpClient.loginRequest(authentication = authentication)

            context.settingsDataStore.updateData {
                it.copy {
                    this.username = authentication.username
                    this.password = authentication.password
                }
            }

            onLogin()
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            onLoginError(error)
        }
    }
}
