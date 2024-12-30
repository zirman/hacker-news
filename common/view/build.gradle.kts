@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("kmplibrary")
    alias(libs.plugins.kotlinxParcelize)
    id("hackernews.detekt")
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":common:domain"))
            compileOnly(libs.koinCore)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            api(libs.koinAnnotations)
            implementation(libs.bundles.ktor)
            implementation(libs.jetbrainsLifecycleRuntimeCompose)
            implementation(libs.jetbrainsLifecycleViewmodel)
            implementation(libs.jetbrainsLifecycleViewmodelCompose)
            implementation(libs.jetbrainsNavigationCompose)
            implementation(libs.jetbrainsCore)
            implementation(libs.jetbrainsWindowCore)
            implementation(libs.jetbrainsSavedState)
            implementation(libs.material3WindowSizeClassMultiplatform)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
        }
        androidMain.dependencies {
            implementation(libs.material3AdaptiveNavigation)
            implementation(libs.bundles.androidxCompose)
            implementation(libs.ktorClientAndroid)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
        }
        jvmMain.dependencies {
            implementation(libs.kotlinxCoroutinesSwing)
            implementation(libs.ktorClientJava)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
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
