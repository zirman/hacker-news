plugins {
    id("buildsrc.convention.kotlin-multiplatform-core")
}
val packageNamespace = "com.monoid.hackernews.common.injection"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
}
