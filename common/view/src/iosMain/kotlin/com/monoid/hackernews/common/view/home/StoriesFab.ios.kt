package com.monoid.hackernews.common.view.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoriesFab(
    fabAction: FabAction,
    expanded: Boolean,
    onClick: (FabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            ExtendedFloatingActionButton(
                onClick = { fabMenuExpanded = fabMenuExpanded.not() },
                expanded = expanded || fabMenuExpanded,
                icon = { Icon(fabAction.icon, null) },
                text = { Text(stringResource(fabAction.text)) },
            )
        },
        modifier = modifier,
    ) {
        // top content padding
        Spacer(
            modifier = Modifier.height(
                with(LocalDensity.current) {
                    WindowInsets.safeContent.getTop(this).toDp()
                },
            ),
        )
        for (i in FabAction.entries.indices) {
            val item = FabAction.entries[i]
            FloatingActionButtonMenuItem(
                modifier = Modifier.semantics {
                    isTraversalGroup = true
                    // Add a custom a11y action to allow closing the menu when focusing
                    // the last menu item, since the close button comes before the first
                    // menu item in the traversal order.
                    if (i == FabAction.entries.size - 1) {
                        customActions = listOf(
                            CustomAccessibilityAction(
                                label = "Close menu",
                                action = {
                                    fabMenuExpanded = false
                                    true
                                },
                            )
                        )
                    }
                },
                onClick = {
                    onClick(item)
                    fabMenuExpanded = false
                },
                icon = { Icon(item.icon, contentDescription = null) },
                text = { Text(text = stringResource(item.text)) },
            )
        }
    }
}
