package com.monoid.hackernews.view.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monoid.hackernews.common.data.Password
import com.monoid.hackernews.common.data.Username
import com.monoid.hackernews.common.view.R
import com.monoid.hackernews.common.view.rememberAnnotatedHtmlString
import com.monoid.hackernews.view.text.PasswordTextField
import com.monoid.hackernews.view.text.UsernameTextField

@Composable
fun LoginContent(
    windowSizeClass: WindowSizeClass,
    onSubmit: (Username, Password) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        contentColor = MaterialTheme.colorScheme.tertiary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    WindowInsets.safeContent
                        .only(run {
                            var windowInsets = WindowInsetsSides.Bottom

                            if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
                                windowInsets += WindowInsetsSides.Top
                            }

                            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                                windowInsets += WindowInsetsSides.Horizontal
                            }

                            windowInsets
                        })
                        .asPaddingValues(),
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val rowModifier: Modifier =
                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                } else {
                    Modifier.width(320.dp)
                }.padding(vertical = 4.dp)

            var username by rememberSaveable { mutableStateOf("") }

            var password by rememberSaveable { mutableStateOf("") }

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
                onUsernameChange = { username = it },
                modifier = rowModifier,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onPrev = { focusManager.moveFocus(FocusDirection.Up) },
            )

            PasswordTextField(
                password = password,
                onChangePassword = { password = it },
                modifier = rowModifier,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onPrev = { focusManager.moveFocus(FocusDirection.Up) },
                onDone = {
                    if (username.isNotBlank() && password.isNotEmpty()) {
                        onSubmit(Username(username), Password(password))
                    }
                },
            )

            val acceptTermsState = rememberSaveable {
                mutableStateOf(false)
            }

            Row(
                modifier = rowModifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = acceptTermsState.value,
                    onCheckedChange = {
                        acceptTermsState.value = acceptTermsState.value.not()
                    },
                )
                val htmlString = stringResource(id = R.string.i_agree_html)
                Text(
                    text = rememberAnnotatedHtmlString(htmlString),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LocalContentColor.current,
                    ),
                )
            }

            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotEmpty()) {
                        onSubmit(Username(username), Password(password))
                    }
                },
                modifier = rowModifier,
                enabled = username.isNotBlank() &&
                    password.isNotEmpty() &&
                    acceptTermsState.value,
            ) {
                Text(text = stringResource(id = R.string.submit))
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
