package com.monoid.hackernews.view.settings

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.monoid.hackernews.common.data.Shape
import com.monoid.hackernews.view.theme.toNameId
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShapeButton(
    shape: Shape,
    selected: Boolean,
    onClickShape: (Shape) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cornerBasedShape = MaterialTheme.shapes.medium
    Button(
        onClick = { onClickShape(shape) },
        elevation = if (selected) ButtonDefaults.elevatedButtonElevation() else null,
        modifier = modifier,
        shape = when (shape) {
            Shape.Rounded -> RoundedCornerShape(
                topStart = cornerBasedShape.topStart,
                topEnd = cornerBasedShape.topEnd,
                bottomEnd = cornerBasedShape.bottomEnd,
                bottomStart = cornerBasedShape.bottomStart,
            )

            Shape.Cut -> CutCornerShape(
                topStart = cornerBasedShape.topStart,
                topEnd = cornerBasedShape.topEnd,
                bottomEnd = cornerBasedShape.bottomEnd,
                bottomStart = cornerBasedShape.bottomStart,
            )
        },
    ) {
        Text(text = stringResource(shape.toNameId()))
    }
}
