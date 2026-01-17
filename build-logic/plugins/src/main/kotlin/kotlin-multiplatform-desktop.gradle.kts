@file:OptIn(DelicateMetroGradleApi::class)

import dev.zacsweers.metro.gradle.DelicateMetroGradleApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("org.jetbrains.compose.hot-reload")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.metro")
    id("detekt-rules")
}
val libs = the<VersionCatalogsExtension>().named("libs")
kotlin {
    jvm("desktop") {
        JvmTarget.fromTarget(libs.findVersion("jvmTarget").get().requiredVersion)
    }
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.findVersion("jvmToolchain").get().requiredVersion.toInt())
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
        }
        commonTest.dependencies {
            implementation(libs.findBundle("commonTest").get())
            implementation(libs.findLibrary("composeUiTest").get())
        }
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
metro {
    enableTopLevelFunctionInjection = true
    generateContributionHintsInFir = true
    enableKotlinVersionCompatibilityChecks = false
}
