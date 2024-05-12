package com.monoid.hackernews.common.injection

actual fun firebaseFactory(): FirebaseAdapter {
    return FirebaseAdapterStub()
}
