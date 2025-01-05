plugins {
    id("buildsrc.convention.kotlin-multiplatform-core")
}
val packageNamespace = "com.monoid.hackernews.common.core"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
}
