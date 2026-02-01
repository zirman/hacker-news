package com.monoid.hackernews

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.Intent
import androidx.annotation.Keep
import androidx.core.app.AppComponentFactory
import com.monoid.hackernews.common.core.metro.AndroidAppGraph
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

/**
 * An [AppComponentFactory] that uses Metro for constructor injection of Activities.
 *
 * If you have minSdk < 28, you can fall back to using member injection on Activities or (better)
 * use an architecture that abstracts the Android framework components away.
 */
@Keep
abstract class AndroidAppComponentFactory : AppComponentFactory() {
    abstract fun createAndroidAppGraph(): AndroidAppGraph

    override fun instantiateApplicationCompat(cl: ClassLoader, className: String): Application {
        appGraph = createAndroidAppGraph()
        return appGraph.application
    }

    override fun instantiateActivityCompat(cl: ClassLoader, className: String, intent: Intent?): Activity {
        val activityGraphProvider = getProvider(cl, className, appGraph.activityGraphProviders)
        return if (activityGraphProvider != null) {
            activityGraphProvider().activity
        } else {
            super.instantiateActivityCompat(cl, className, intent)
        }
    }

    override fun instantiateServiceCompat(cl: ClassLoader, className: String, intent: Intent?): Service {
        val serviceGraphProvider = getProvider(cl, className, appGraph.serviceGraphProviders)
        return if (serviceGraphProvider != null) {
            serviceGraphProvider().service
        } else {
            super.instantiateServiceCompat(cl, className, intent)
        }
    }

    override fun instantiateReceiverCompat(cl: ClassLoader, className: String, intent: Intent?): BroadcastReceiver {
        val broadcastReceiverGraphProvider =
            getProvider(cl, className, appGraph.broadcastReceiverGraphProviders)
        return if (broadcastReceiverGraphProvider != null) {
            broadcastReceiverGraphProvider().broadcastReceiver
        } else {
            super.instantiateReceiverCompat(cl, className, intent)
        }
    }

    override fun instantiateProviderCompat(cl: ClassLoader, className: String): ContentProvider {
        val broadcastReceiverGraphProvider =
            getProvider(cl, className, appGraph.contentProviderGraphProviders)
        return if (broadcastReceiverGraphProvider != null) {
            broadcastReceiverGraphProvider().contentProvider
        } else {
            super.instantiateProviderCompat(cl, className)
        }
    }

    private inline fun <reified T : Any, R> getProvider(
        cl: ClassLoader,
        className: String,
        providers: Map<KClass<out T>, Provider<R>>,
    ): Provider<R>? {
        val kClass = Class.forName(className, false, cl).asSubclass(T::class.java).kotlin
        return providers[kClass]
    }

    // AppComponentFactory can be created multiple times
    companion object {
        private lateinit var appGraph: AndroidAppGraph
    }
}
