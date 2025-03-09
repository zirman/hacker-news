package com.monoid.hackernews.common.view.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.password
import com.monoid.hackernews.common.view.password_toggle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordTextField(
    password: String,
    enabled: Boolean,
    onChangePassword: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelId: StringResource = Res.string.password,
    errorLabelId: StringResource? = null,
    onNext: (() -> Unit)? = null,
    onPrev: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        val (isPasswordVisible, setPasswordVisible) =
            rememberSaveable { mutableIntStateOf(0) }

        OutlinedTextField(
            value = password,
            onValueChange = onChangePassword,
            modifier = Modifier
                .semantics {
                    contentType = ContentType.Password
                }
                .onPreviewKeyEvent { event -> // handle hardware keyboards
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                    when (event.key) {
                        Key.Tab -> {
                            if (event.isShiftPressed) {
                                onPrev?.invoke()
                            } else {
                                onNext?.invoke()
                            }

                            true
                        }

                        Key.Enter -> {
                            onDone?.invoke()
                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
                .fillMaxWidth(),
            enabled = enabled,
            label = { Text(text = stringResource(labelId)) },
            placeholder = { Text(text = stringResource(Res.string.password)) },
            trailingIcon = {
                IconToggleButton(
                    checked = isPasswordVisible != 0,
                    onCheckedChange = { setPasswordVisible(if (it) 1 else 0) },
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible != 0) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = stringResource(Res.string.password_toggle),
                    )
                }
            },
            visualTransformation = if (isPasswordVisible != 0) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = errorLabelId != null,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = if (onNext != null) ImeAction.Next else ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onNext = onNext?.let { { onNext() } },
                onDone = onDone?.let { { onDone() } },
            ),
            singleLine = true,
        )

        if (errorLabelId != null) {
            Text(
                text = stringResource(errorLabelId),
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
    }
}
