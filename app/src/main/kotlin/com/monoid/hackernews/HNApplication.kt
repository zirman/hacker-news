package com.monoid.hackernews

import android.app.Application
import androidx.room.Room
import com.monoid.hackernews.room.HNDatabase

class HNApplication : Application() {
    companion object {
        lateinit var instance: HNApplication
            private set
    }

    lateinit var db: HNDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        db = Room
            .databaseBuilder(
                applicationContext,
                HNDatabase::class.java,
                "hacker-news-database"
            )
            .build()
    }
}