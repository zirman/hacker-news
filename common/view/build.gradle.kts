plugins {
    id("kotlin-multiplatform-library")
}
val packageNamespace = "com.monoid.hackernews.common.view"
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":domain"))
        }
    }
    android {
        namespace = packageNamespace
        androidResources.enable = true
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
