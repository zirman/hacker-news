package com.monoid.hackernews.common.view.text

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.monoid.hackernews.common.view.Res
import com.monoid.hackernews.common.view.clear
import com.monoid.hackernews.common.view.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun UsernameTextField(
    username: String,
    enabled: Boolean,
    onUsernameChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        modifier = modifier
            .semantics {
                contentType = ContentType.Username
            }
            .onPreviewKeyEvent { event -> // handle hardware keyboards
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.key) {
                    Key.Tab, Key.Enter -> {
                        if (event.isShiftPressed) {
                            onPrev()
                        } else {
                            onNext()
                        }

                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            .fillMaxWidth(),
        enabled = enabled,
        label = { Text(text = stringResource(Res.string.username)) },
        placeholder = { Text(text = stringResource(Res.string.username)) },
        trailingIcon = {
            if (username.isNotBlank()) {
                IconButton(onClick = { onUsernameChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(Res.string.clear)
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        singleLine = true
    )
}
