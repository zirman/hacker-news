plugins {
    id("kotlin-multiplatform-library")
    id("org.jetbrains.kotlin.plugin.serialization")
}
val packageNamespace = "com.monoid.hackernews.common.domain"
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":data"))
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
