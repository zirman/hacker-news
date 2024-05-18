plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
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
            implementation(libs.lifecycleViewModelKtx)
            implementation(libs.datastore)
            implementation(libs.datastorePreferences)
            implementation(libs.bundles.ktor)

            implementation(libs.roomRuntime)
            implementation(libs.roomKtx)
            implementation(libs.sqliteBundled)

            implementation(libs.kermit)

            implementation(libs.annotation)

            implementation(libs.collectionKtx)

            implementation(project(":common:injection"))
        }

        commonTest.dependencies {
            implementation(libs.kotlinTest)
        }

        androidMain.dependencies {
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
        }

        jvmMain.dependencies {
            implementation(libs.ktorClientJava)
        }
    }
}

android {
    namespace = "com.monoid.hackernews.common.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

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
    add("kspAndroid", libs.roomCompiler)
    add("kspJvm", libs.roomCompiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
