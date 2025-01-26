//@file:OptIn(ExperimentalResourceApi::class)
//
//package com.monoid.hackernews.common.view.itemlist
//
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalInspectionMode
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onRoot
//import androidx.compose.ui.text.AnnotatedString
//import com.dropbox.differ.SimpleImageComparator
//import com.github.takahirom.roborazzi.RoborazziOptions
//import com.github.takahirom.roborazzi.RoborazziRule
//import com.github.takahirom.roborazzi.captureRoboImage
//import com.monoid.hackernews.common.data.api.ItemId
//import com.monoid.hackernews.common.data.model.ItemType
//import com.monoid.hackernews.common.data.model.makeItem
//import org.jetbrains.compose.resources.ExperimentalResourceApi
//import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
//import org.junit.Rule
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.GraphicsMode
//import kotlin.test.Test
//
//@RunWith(RobolectricTestRunner::class)
//@GraphicsMode(GraphicsMode.Mode.NATIVE)
//class ScreenshotTest {
//
//    @get:Rule
//    val roborazziRule: RoborazziRule = RoborazziRule(
//        options = RoborazziRule.Options(
//            roborazziOptions = RoborazziOptions(
//                compareOptions = RoborazziOptions.CompareOptions(
//                    imageComparator = SimpleImageComparator(
//                        maxDistance = 0.0021f,
//                    ),
//                ),
//            ),
//            outputDirectoryPath = "src/androidUnitTest/screenshotTest",
//        ),
//    )
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    @Test
//    fun `ItemsColumn Screenshot`(): Unit = composeTestRule.run {
//        setContent {
//            CompositionLocalProvider(LocalInspectionMode provides true) {
//                PreviewContextConfigurationEffect()
//                MaterialTheme {
//                    ItemsColumn(
//                        itemsList = listOf(
//                            makeItem(
//                                id = ItemId(0),
//                                lastUpdate = 123,
//                                type = ItemType.Story,
//                                time = 123,
//                                deleted = false,
//                                by = "Jane Doe",
//                                descendants = 0,
//                                score = 5,
//                                title = AnnotatedString("Hello World."),
//                                text = null,
//                                url = "https://www.wikipedia.org/",
//                                parent = null,
//                                kids = emptyList(),
//                                upvoted = false,
//                                favourited = false,
//                                flagged = false,
//                                expanded = true,
//                                followed = false,
//                            ),
//                        ),
//                        onVisibleItem = {},
//                        onClickItem = {},
//                        onClickReply = {},
//                        onClickUser = {},
//                        onOpenUrl = {},
//                        onClickUpvote = {},
//                        onClickFavorite = {},
//                        onClickFollow = {},
//                        onClickFlag = {},
//                        listState = rememberLazyListState(),
//                        modifier = Modifier.fillMaxHeight(),
//                    )
//                }
//            }
//        }
//        onRoot().captureRoboImage()
//    }
//}
