package com.monoid.hackernews.common

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.okio.OkioSerializer
import com.monoid.hackernews.common.data.Authentication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import okio.BufferedSink
import okio.BufferedSource
import org.koin.core.module.Module

internal val dataStoreFileName = "hackernews.preferences_pb"

expect val dataStoreModule: Module
