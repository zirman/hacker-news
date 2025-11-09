plugins {
    id("kotlin-multiplatform-library")
    id("org.jetbrains.kotlin.plugin.serialization")
}
val packageNamespace = "com.monoid.hackernews.common.domain"
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":common-data"))
        }
    }
    androidLibrary {
        namespace = packageNamespace
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
