import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.jvmTarget.get()
            }
        }
    }

    dependencies {
        implementation(platform(libs.kotilnxCoroutinesBom))
        implementation(platform(libs.kotlinWrappersBom))
        implementation(platform(libs.koinBom))
        implementation(platform(libs.firebaseBom))
        implementation(platform(libs.composeBom))
        compileOnly(libs.koinCore)
        coreLibraryDesugaring(libs.desugarJdkLibsNio)
        implementation(libs.bundles.androidxCompose)
        implementation(libs.bundles.kotlinx)
        implementation(libs.bundles.koin)
    }

    sourceSets {
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(libs.kotlinTest)
        }

        androidMain.dependencies {
            implementation(libs.bundles.firebase)
        }

        nativeMain.dependencies {
        }
    }
}

android {
    namespace = "com.monoid.hackernews.common.injection"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        allWarningsAsErrors = false
    }
}
