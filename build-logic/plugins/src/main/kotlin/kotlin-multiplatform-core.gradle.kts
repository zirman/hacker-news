@file:Suppress("OPT_IN_USAGE")

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
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
    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(libs.findLibrary("kotlinWrappersBom").get()))
            api(project.dependencies.platform(libs.findLibrary("firebaseBom").get()))
            api(compose.animation)
            api(compose.animationGraphics)
            api(compose.components.resources)
            api(compose.foundation)
            api(compose.material3)
            api(compose.materialIconsExtended)
            api(compose.runtime)
            api(compose.ui)
            api(compose.uiUtil)
            api(libs.findBundle("commonMain").get())
        }
        androidMain.dependencies {
            api(project.dependencies.platform(libs.findLibrary("kotlinCoroutinesBom").get()))
            api(compose.uiTooling)
            api(compose.preview)
            api(compose.components.uiToolingPreview)
            api(libs.findBundle("androidMain").get())
        }
        jvmMain.dependencies {
            api(project.dependencies.platform(libs.findLibrary("kotlinCoroutinesBom").get()))
            api(compose.desktop.common)
            api(compose.desktop.currentOs)
            @OptIn(ExperimentalComposeLibrary::class)
            api(compose.desktop.components.animatedImage)
            @OptIn(ExperimentalComposeLibrary::class)
            api(compose.desktop.components.splitPane)
            api(libs.findBundle("jvmMain").get())
        }
        iosMain.dependencies {
            api(libs.findBundle("iosMain").get())
        }
        commonTest.dependencies {
            implementation(libs.findBundle("commonTest").get())
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        androidUnitTest {
            kotlin.srcDir("build/generated/ksp/android/androidDebug/screenshotTest")
        }
        androidUnitTest.dependencies {
            implementation(libs.findBundle("androidUnitTest").get())
        }
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
    androidLibrary {
        compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()
        compileSdkPreview = libs.findVersion("compileSdkPreview").get().requiredVersion
        minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
        buildToolsVersion = libs.findVersion("buildToolsVersion").get().requiredVersion
        enableCoreLibraryDesugaring = true
        packaging {
            resources {
                excludes += "/META-INF/versions/9/previous-compilation-data.bin"
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        lint {
            warningsAsErrors = true
            baseline = file("lint-baseline.xml")
        }
        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.fromTarget(libs.findVersion("jvmTarget").get().requiredVersion))
            }
        }
    }
    jvm()
//    iosX64()
    iosArm64()
    iosSimulatorArm64()
    compilerOptions {
        // Should be able to remove in 2.2.20-Beta2
        // https://issuetracker.google.com/issues/429988549
        // apiVersion = KOTLIN_2_1
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.findVersion("jvmToolchain").get().requiredVersion.toInt())
}
val kspAndroid by configurations.named("kspAndroid")
dependencies {
    coreLibraryDesugaring(libs.findLibrary("desugarJdkLibsNio").get())
    // https://github.com/google/ksp/issues/2595
    lintChecks(libs.findLibrary("composeLintChecks").get())
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
roborazzi {
    outputDir.set(file("src/androidUnitTest/screenshotTest"))
    // Directory for comparison images (Experimental option)
    compare {
        outputDir.set(file("build/roborazzi/comparison"))
    }
}
