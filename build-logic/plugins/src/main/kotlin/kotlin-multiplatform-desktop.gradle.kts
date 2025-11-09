import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    kotlin("multiplatform")
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
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common-core"))
        }
        commonTest.dependencies {
            implementation(libs.findBundle("commonTest").get())
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
    jvm("desktop")
    compilerOptions {
        // Should be able to remove in 2.2.20-Beta2
        // https://issuetracker.google.com/issues/429988549
        // apiVersion = KOTLIN_2_1
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.findVersion("jvmToolchain").get().requiredVersion.toInt())
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
