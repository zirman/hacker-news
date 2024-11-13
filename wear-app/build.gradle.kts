plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.googlePlayServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    id("hackernews.detekt")
}
kotlin {
    compilerOptions {
        extraWarnings.set(true)
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}
android {
    namespace = "com.monoid.hackernews.wear"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
    buildToolsVersion = libs.versions.buildToolsVersion.get()
    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "sn13p9hDhpAU"
            keyAlias = "release"
            keyPassword = "h8G8xDZYuceM"
        }
    }
    defaultConfig {
        applicationId = "com.monoid.hackernews.wear"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        targetSdkPreview = libs.versions.targetSdk.get()
        versionCode = 1
        versionName = "1.0"
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
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)
    compileOnly(libs.koinCore)
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidxWear)
    implementation(libs.bundles.datastore)
    implementation(libs.bundles.google)
    implementation(libs.bundles.googleWear)
    implementation(libs.bundles.ktor)
    implementation(libs.koinAndroid)
    implementation(libs.koinAnnotations)
    implementation(libs.lifecycleProcess)
    implementation(libs.slf4jSimple)
    implementation(platform(libs.koinBom))
    implementation(project(":common:injection"))
    implementation(project(":common:view"))
    lintChecks(libs.composeLintChecks)
}
//dependencies {
//    add("kspCommonMainMetadata", libs.koinKspCompiler)
//    add("kspAndroid", libs.koinKspCompiler)
//}
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
// Trigger Common Metadata Generation from Native tasks
//project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
//    if (name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}
