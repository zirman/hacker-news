package buildsrc.convention

import com.android.build.gradle.tasks.asJavaVersion
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("buildsrc.convention.detekt-rules")
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
}
val libs = the<LibrariesForLibs>()
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common:core"))
        }
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
    }
    jvm()
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    buildToolsVersion = libs.versions.buildToolsVersion.get()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility =
            JavaLanguageVersion.of(libs.versions.jvmTarget.get()).asJavaVersion()
        targetCompatibility =
            JavaLanguageVersion.of(libs.versions.jvmTarget.get()).asJavaVersion()
    }
    sourceSets.named("main").get().apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
        resources.srcDirs("src/commonMain/resources")
    }
    packaging {
        resources {
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    kspCommonMainMetadata(libs.koinKspCompiler)
    "kspAndroid"(libs.koinKspCompiler)
    "kspJvm"(libs.koinKspCompiler)
    "kspIosX64"(libs.koinKspCompiler)
    "kspIosArm64"(libs.koinKspCompiler)
    "kspIosSimulatorArm64"(libs.koinKspCompiler)
    lintChecks(libs.composeLintChecks)
}
ksp {
    arg("KOIN_CONFIG_CHECK", "false")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
compose {
    resources {
        publicResClass = true
        generateResClass = always
    }
}
// Trigger Common Metadata Generation from Native tasks
tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}