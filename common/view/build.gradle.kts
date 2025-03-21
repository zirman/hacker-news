plugins {
    id("buildsrc.convention.kotlin-multiplatform-library")
    id("kotlin-parcelize")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":common:domain"))
        }
    }
}
val packageNamespace = "com.monoid.hackernews.common.view"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
}
