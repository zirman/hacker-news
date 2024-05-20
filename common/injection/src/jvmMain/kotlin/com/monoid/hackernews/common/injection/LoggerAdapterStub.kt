package com.monoid.hackernews.common.injection

import co.touchlab.kermit.Logger

class LoggerAdapterStub : LoggerAdapter {
    override fun recordException(messageString: String, throwable: Throwable, tag: String?) {
        if (tag != null) {
            Logger.e(messageString, throwable, tag)
        } else {
            Logger.e(messageString, throwable)
        }
    }
}
