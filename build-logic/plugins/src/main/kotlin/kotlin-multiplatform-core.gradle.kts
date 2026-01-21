@file:Suppress("OPT_IN_USAGE")
@file:OptIn(DelicateMetroGradleApi::class)

import dev.zacsweers.metro.gradle.DelicateMetroGradleApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("dev.zacsweers.metro")
    id("io.github.takahirom.roborazzi")
    id("detekt-rules")
}
val libs = the<VersionCatalogsExtension>().named("libs")
kotlin {
    android {
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
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.findVersion("jvmTarget").get().requiredVersion))
        }
    }
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.findVersion("jvmTarget").get().requiredVersion))
        }
    }
    iosArm64()
    iosSimulatorArm64()
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
    jvmToolchain(libs.findVersion("jvmToolchain").get().requiredVersion.toInt())
    sourceSets {
        commonMain.dependencies {
            api(project.dependencies.platform(libs.findLibrary("kotlinCoroutinesBom").get()))
            api(project.dependencies.platform(libs.findLibrary("kotlinWrappersBom").get()))
            api(project.dependencies.platform(libs.findLibrary("firebaseBom").get()))
            api(libs.findBundle("commonMain").get())
        }
        androidMain.dependencies {
            api(libs.findBundle("androidMain").get())
        }
        jvmMain.dependencies {
            api(libs.findBundle("jvmMain").get())
            api(compose.desktop.currentOs)
        }
        iosMain.dependencies {
            api(libs.findBundle("iosMain").get())
        }
        commonTest.dependencies {
            implementation(libs.findBundle("commonTest").get())
        }
//        commonTest {
//            kotlin.srcDir("build/generated/ksp/android/androidDebug/screenshotTest")
//            // dependsOn(commonMain.get())
//            dependencies {
//                implementation(libs.findBundle("commonTest").get())
//            }
//        }
    }
}
dependencies {
    coreLibraryDesugaring(libs.findLibrary("desugarJdkLibsNio").get())
    kspAndroid(project(":injection-processor"))
    kspAndroid(project(":screenshot-processor"))
    lintChecks(libs.findLibrary("composeLintChecks").get())
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
    outputDir.set(file("src/commonTest/screenshotTest"))
    // Directory for comparison images (Experimental option)
    compare {
        outputDir.set(file("build/roborazzi/comparison"))
    }
}
