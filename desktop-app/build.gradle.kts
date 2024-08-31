@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    id("hackernews.detekt")
}
kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    jvm("desktop") { }
    sourceSets {
        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinxCoroutinesSwing)
            implementation(libs.ktorClientJava)
        }
        commonMain.dependencies {
            implementation(compose.animation)
            implementation(compose.animationGraphics)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.desktop.common)
            implementation(compose.desktop.components.animatedImage)
            implementation(compose.desktop.components.splitPane)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.preview)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.uiTooling)
            implementation(compose.uiUtil)
            implementation(libs.jetbrainsLifecycleViewmodel)
            implementation(libs.jetbrainsLifecycleViewmodelCompose)
            implementation(libs.jetbrainsLifecycleRuntimeCompose)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.koin)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(project(":common:injection"))
            implementation(project(":common:view"))
        }
    }
}
compose.desktop {
    application {
        mainClass = "com.monoid.hackernews.Main_desktopKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.monoid.hackernews"
            packageVersion = "1.0.0"
        }
    }
}
