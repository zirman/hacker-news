plugins {
    id("kotlin-multiplatform-core")
}
val packageNamespace = "com.monoid.hackernews.common.core"
kotlin {
    sourceSets {
        androidMain.dependencies {
            api(project(":injection"))
        }
    }
    android {
        namespace = packageNamespace
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
