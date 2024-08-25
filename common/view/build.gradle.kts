plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    id("hackernews.detekt")
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    androidTarget { }
    jvm { }
    sourceSets {
        androidMain.dependencies {
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
            implementation(compose.material3)
        }
        jvmMain.dependencies { }
        commonMain.dependencies {
            api(project(":common:domain"))
            compileOnly(libs.koinCore)
            implementation(compose.components.resources)
            implementation(compose.material3)
            implementation(libs.annotation)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
            implementation(libs.collectionKtx)
            implementation(libs.navigationCompose)
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
        }
        commonTest.dependencies {
            implementation(libs.bundles.test)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.monoid.hackernews.common.view"
    generateResClass = always
}

android {
    namespace = "com.monoid.hackernews.common.view"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        debug { }
        release { }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
    }
}
