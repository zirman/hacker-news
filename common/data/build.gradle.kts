plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("hackernews.detekt")
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
            implementation(libs.datastore)
            implementation(libs.datastorePreferences)
            implementation(libs.bundles.ktor)

            implementation(libs.roomRuntime)
            implementation(libs.roomKtx)
            implementation(libs.sqliteBundled)

            implementation(libs.annotation)

            implementation(libs.collectionKtx)

            implementation(project(":common:injection"))
        }

        commonTest.dependencies {
            implementation(libs.kotlinTest)
        }

        androidMain.dependencies {
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
            implementation(libs.koinAndroid)
        }

        jvmMain.dependencies {
            implementation(libs.ktorClientJava)
        }
    }
}

android {
    namespace = "com.monoid.hackernews.common.data"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileSdkPreview = libs.versions.compileSdkPreview.get()

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
}

dependencies {
//    ksp(libs.roomCompiler)
    add("kspAndroid", libs.roomCompiler)
    add("kspJvm", libs.roomCompiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
