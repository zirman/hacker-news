package com.monoid.hackernews.view.text

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
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.monoid.hackernews.common.view.R

@Composable
fun UsernameTextField(
    username: String,
    onUsernameChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val autofillNode = AutofillNode(
        autofillTypes = listOf(AutofillType.Username),
        onFill = onUsernameChange
    )

    val autofill = LocalAutofill.current
    LocalAutofillTree.current += autofillNode

    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        modifier = modifier
            .onGloballyPositioned { autofillNode.boundingBox = it.boundsInWindow() }
            .onFocusChanged { focusState ->
                autofill?.run {
                    if (focusState.isFocused) {
                        requestAutofillForNode(autofillNode)
                    } else {
                        cancelAutofillForNode(autofillNode)
                    }
                }
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
            },
        label = { Text(text = stringResource(id = R.string.username)) },
        placeholder = { Text(text = stringResource(id = R.string.username)) },
        trailingIcon = {
            if (username.isNotBlank()) {
                IconButton(onClick = { onUsernameChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(id = R.string.clear)
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
