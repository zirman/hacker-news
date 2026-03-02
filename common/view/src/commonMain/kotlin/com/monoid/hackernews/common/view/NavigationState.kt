package com.monoid.hackernews.common.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.monoid.hackernews.common.domain.navigation.BottomNav
import com.monoid.hackernews.common.domain.navigation.Route
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * Create a navigation state that persists config changes and process death.
 *
 * @param startRoute - The top level route to start on. This should also be in `topLevelRoutes`.
 * @param topLevelRoutes - The top level routes in the app.
 */
@Composable
fun rememberNavigationState(
    startRoute: BottomNav,
    topLevelRoutes: Set<BottomNav>,
): NavigationState {
    val topLevelRoute: MutableState<BottomNav> = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(),
    ) {
        mutableStateOf(startRoute)
    }
    // Create a back stack for each top level route.
    val backStacks: Map<BottomNav, NavBackStack<NavKey>> = topLevelRoutes.associateWith { key ->
        rememberNavBackStack(
            configuration = SavedStateConfiguration {
                serializersModule = SerializersModule {
                    polymorphic(NavKey::class) {
                        subclass(BottomNav.Stories::class, BottomNav.Stories.serializer())
                        subclass(BottomNav.Favorites::class, BottomNav.Favorites.serializer())
                        subclass(BottomNav.Settings::class, BottomNav.Settings.serializer())
                        subclass(Route.User::class, Route.User.serializer())
                        subclass(Route.Story::class, Route.Story.serializer())
                        subclass(Route.Reply::class, Route.Reply.serializer())
                        subclass(
                            Route.Settings.Appearance::class,
                            Route.Settings.Appearance.serializer()
                        )
                        subclass(
                            Route.Settings.Notifications::class,
                            Route.Settings.Notifications.serializer()
                        )
                        subclass(Route.Settings.Help::class, Route.Settings.Help.serializer())
                        subclass(
                            Route.Settings.TermsOfService::class,
                            Route.Settings.TermsOfService.serializer()
                        )
                        subclass(
                            Route.Settings.UserGuidelines::class,
                            Route.Settings.UserGuidelines.serializer()
                        )
                        subclass(
                            Route.Settings.SendFeedback::class,
                            Route.Settings.SendFeedback.serializer()
                        )
                        subclass(Route.Settings.About::class, Route.Settings.About.serializer())
                    }
                }
            },
            key,
        )
    }
    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}

/**
 * State holder for navigation state. This class does not modify its own state. It is designed
 * to be modified using the `Navigator` class.
 *
 * @param startRoute - the start route. The user will exit the app through this route.
 * @param topLevelRoute - the state object that backs the top level route.
 * @param backStacks - the back stacks for each top level route.
 */
@Stable
class NavigationState(
    val startRoute: BottomNav,
    topLevelRoute: MutableState<BottomNav>,
    val backStacks: Map<BottomNav, NavBackStack<NavKey>>,
) {
    /**
     * The top level route.
     */
    var topLevelRoute: BottomNav by topLevelRoute

    /**
     * Convert the navigation state into `NavEntry`s that have been decorated with a
     * `SaveableStateHolder`.
     *
     * @param entryProvider - the entry provider used to convert the keys in the
     * back stacks to `NavEntry`s.
     */
    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>,
    ): SnapshotStateList<NavEntry<NavKey>> {
        // For each back stack, create a `SaveableStateHolder` decorator and use it to decorate
        // the entries from that stack. When backStacks changes, `rememberDecoratedNavEntries` will
        // be recomposed and a new list of decorated entries is returned.
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            val decorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            )
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = decorators,
                entryProvider = entryProvider,
            )
        }
        // Only return the entries for the stacks that are currently in use.
        return getTopLevelRoutesInUse()
            .flatMap { decoratedEntries[it] ?: emptyList() }
            .toMutableStateList()
    }

    /**
     * Get the top level routes that are currently in use. The start route is always the first route
     * in the list. This means the user will always exit the app through the starting route
     * ("exit through home" pattern). The list will contain a maximum of one other route. This is a
     * design decision. In your app, you may wish to allow more than two top level routes to be
     * active.
     *
     * Note that even if a top level route is not in use its state is still retained.
     *
     * @return the current top level routes that are in use.
     */
    private fun getTopLevelRoutesInUse(): List<NavKey> =
        if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }
}
