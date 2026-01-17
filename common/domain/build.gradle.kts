plugins {
    id("kotlin-multiplatform-library")
    id("org.jetbrains.kotlin.plugin.serialization")
}
val packageNamespace = "com.monoid.hackernews.common.domain"
kotlin {
    android {
        namespace = packageNamespace
    }
    sourceSets {
        commonMain.dependencies {
            api(project(":data"))
        }
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
