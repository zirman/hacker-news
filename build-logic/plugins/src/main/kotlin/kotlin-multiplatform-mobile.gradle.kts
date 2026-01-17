@file:Suppress("OPT_IN_USAGE")

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("org.jetbrains.compose.hot-reload")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.metro")
    id("io.github.takahirom.roborazzi")
    id("detekt-rules")
}
val libs = the<VersionCatalogsExtension>().named("libs")
kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
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
roborazzi {
    outputDir.set(file("src/androidUnitTest/screenshotTest"))
    // Directory for comparison images (Experimental option)
    compare {
        outputDir.set(file("build/roborazzi/comparison"))
    }
}
