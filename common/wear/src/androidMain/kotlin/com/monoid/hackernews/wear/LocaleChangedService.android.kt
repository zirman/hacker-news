package com.monoid.hackernews.wear

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.monoid.hackernews.common.core.metro.ServiceGraph
import com.monoid.hackernews.common.core.metro.ServiceKey
import com.monoid.hackernews.common.core.metro.ServiceScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@SingleIn(ServiceScope::class)
@Inject
class LocaleChangedService : Service() {
    @GraphExtension(ServiceScope::class)
    interface Graph : ServiceGraph {
        @Binds
        val LocaleChangedService.bind: Service

        @ContributesTo(AppScope::class)
        @GraphExtension.Factory
        interface Factory {
            fun createLocaleChangedServiceGraph(): Graph
        }
    }

    @BindingContainer
    object AppBindings {
        @ServiceKey(LocaleChangedService::class)
        @IntoMap
        @Provides
        fun provideLocaleChangedServiceGraph(graphFactory: Graph.Factory): ServiceGraph =
            graphFactory.createLocaleChangedServiceGraph()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        updateAndPushDynamicShortcuts(MainActivity::class.java)
        stopSelf()
        return START_NOT_STICKY
    }
}
