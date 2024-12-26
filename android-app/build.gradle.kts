@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.googlePlayServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebasePerf)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    id("hackernews.detekt")
}
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    androidTarget {
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.bundles.androidx)
            implementation(libs.bundles.androidxCompose)
            implementation(libs.bundles.androidxApp)
            implementation(libs.koinAndroid)
            implementation(compose.preview)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.uiTooling)
            implementation(libs.ktorClientAndroid)
            implementation(libs.material3Adaptive)
            implementation(libs.material3AdaptiveLayout)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(libs.bundles.googleApp)
            implementation(libs.bundles.google)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.uiTest)
            implementation(compose.uiUtil)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project(":common:injection"))
            implementation(project(":common:view"))
            compileOnly(libs.koinCore)
//            implementation(libs.activityCompose)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.kotlinx)
            implementation(libs.navigationCompose)
            implementation(libs.koinAnnotations)
            implementation(libs.lifecycleProcess)
            implementation(libs.slf4jSimple)
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
    }
    sourceSets.named("commonMain") {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}
compose.resources {
    publicResClass = true
    packageOfResClass = "com.monoid.hackernews"
    generateResClass = always
}
android {
    namespace = "com.monoid.hackernews"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    buildToolsVersion = libs.versions.buildToolsVersion.get()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "sn13p9hDhpAU"
            keyAlias = "release"
            keyPassword = "h8G8xDZYuceM"
        }
    }
    defaultConfig {
        applicationId = "com.monoid.hackernews"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        targetSdkPreview = libs.versions.targetSdk.get()
        versionCode = 44
        versionName = "1.1.5"
        // reduces apk sizes by not including unsupported languages
        resourceConfigurations += setOf("en", "es")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
    }
    packaging {
        resources {
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    add("kspCommonMainMetadata", libs.koinKspCompiler)
    add("kspAndroid", libs.koinKspCompiler)
    add("kspIosX64", libs.koinKspCompiler)
    add("kspIosArm64", libs.koinKspCompiler)
    add("kspIosSimulatorArm64", libs.koinKspCompiler)
}
ksp {
    arg("KOIN_CONFIG_CHECK", "false")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
// Trigger Common Metadata Generation from Native tasks
//project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
//    if (name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}
