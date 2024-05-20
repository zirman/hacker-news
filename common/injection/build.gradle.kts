plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())

    jvm {
    }

    androidTarget {
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.koinBom))
            compileOnly(libs.koinCore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.kermit)
        }

        commonTest.dependencies {
            implementation(libs.kotlinTest)
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebaseBom))
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
            implementation(libs.bundles.firebase)
        }
    }
}

android {
    namespace = "com.monoid.hackernews.common.injection"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
