plugins {
    id("buildsrc.convention.kotlin-multiplatform-library")
    id("kotlin-parcelize")
}
val packageNamespace = "com.monoid.hackernews.common.view"
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":common:domain"))
        }
    }
    androidLibrary {
        namespace = packageNamespace
        androidResources.enable = true
    }
}
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
