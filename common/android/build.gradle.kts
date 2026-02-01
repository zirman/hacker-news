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
        androidMain.dependencies {
            implementation(libs.lifecycleViewmodelNavigation3)
        }
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
