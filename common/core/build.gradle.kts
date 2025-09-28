plugins {
    id("buildsrc.convention.kotlin-multiplatform-core")
}
kotlin {
    sourceSets {
        androidMain.dependencies {
            api(project(":common:injection"))
        }
    }
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
