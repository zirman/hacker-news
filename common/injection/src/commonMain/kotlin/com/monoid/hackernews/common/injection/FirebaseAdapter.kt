package com.monoid.hackernews.common.injection

interface FirebaseAdapter {
    fun recordException(throwable: Throwable)
}
