@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("kmplibrary")
    alias(libs.plugins.kotlinxParcelize)
    id("hackernews.detekt")
}
kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.bundles.androidxCompose)
            implementation(libs.ktorClientAndroid)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(compose.preview)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.uiTooling)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinxCoroutinesSwing)
            implementation(libs.ktorClientJava)
            implementation(compose.desktop.common)
            implementation(compose.desktop.components.animatedImage)
            implementation(compose.desktop.components.splitPane)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            api(project(":common:domain"))
            compileOnly(libs.koinCore)
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(compose.components.resources)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)

            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            api(libs.koinAnnotations)
            implementation(libs.bundles.ktor)
            implementation(libs.jetbrainsLifecycleRuntimeCompose)
            implementation(libs.jetbrainsLifecycleViewmodel)
            implementation(libs.jetbrainsLifecycleViewmodelCompose)
            implementation(libs.navigationCompose)
            implementation(libs.material3WindowSizeClassMultiplatform)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
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
