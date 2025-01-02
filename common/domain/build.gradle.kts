plugins {
    id("buildsrc.convention.kotlin-multiplatform-library")
    id("org.jetbrains.kotlin.plugin.serialization")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":common:data"))
        }
    }
}
val packageNamespace = "com.monoid.hackernews.common.domain"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
}
