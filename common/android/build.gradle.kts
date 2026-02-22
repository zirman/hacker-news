plugins {
    id("kotlin-multiplatform-library")
}
val packageNamespace = "com.monoid.hackernews.common.android"
kotlin {
    android {
        namespace = packageNamespace
        androidResources.enable = true
    }
    sourceSets {
        commonMain.dependencies {
            api(project(":view"))
        }
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
