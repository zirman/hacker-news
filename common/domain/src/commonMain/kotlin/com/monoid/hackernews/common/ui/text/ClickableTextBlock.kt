//package com.monoid.hackernews.common.ui.text
//
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.TextLayoutResult
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.TextUnit
//
//@Composable
//fun ClickableTextBlock(
//    text: AnnotatedString,
//    lines: Int,
//    modifier: Modifier = Modifier,
//    style: TextStyle = TextStyle.Default,
//    softWrap: Boolean = true,
//    overflow: TextOverflow = TextOverflow.Clip,
//    minHeight: Boolean = false,
//    onTextLayout: (TextLayoutResult) -> Unit = {},
//) {
//    val blockHeightSp: TextUnit =
//        style.lineHeight * lines
//
//    val blockHeightDp: Dp =
//        with(LocalDensity.current) { blockHeightSp.toDp() }
//
//    Text(
//        text = text,
//        modifier = if (minHeight) modifier.heightIn(min = blockHeightDp, max = Dp.Infinity)
//        else modifier.height(blockHeightDp),
//        style = style,
//        softWrap = softWrap,
//        overflow = overflow,
//        maxLines = if (minHeight) Int.MAX_VALUE else lines,
//        onTextLayout = onTextLayout,
//    )
//}
