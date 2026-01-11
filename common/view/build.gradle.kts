plugins {
    id("kotlin-multiplatform-library")
}
val packageNamespace = "com.monoid.hackernews.common.view"
kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")
        }
        commonMain.dependencies {
            api(project(":domain"))
        }
        jvmMain.dependencies {
            api("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")
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
