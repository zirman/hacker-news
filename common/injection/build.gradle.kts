plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    id("hackernews.detekt")
}
kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    androidTarget { }
    jvm { }
    sourceSets {
        androidMain.dependencies { }
        jvmMain.dependencies { }
        commonMain.dependencies {
            compileOnly(libs.koinCore)
            implementation(libs.bundles.firebase)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.firebaseBom))
            implementation(libs.kermit)
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
        }
        commonTest.dependencies {
            implementation(libs.bundles.test)
        }
    }
}
android {
    namespace = "com.monoid.hackernews.common.injection"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()
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
