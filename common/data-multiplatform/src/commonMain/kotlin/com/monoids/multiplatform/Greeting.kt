package com.monoids.multiplatform

class Greeting {
    fun greet(): String {
        return "Hello, ${getPlatform()}!"
    }
}
