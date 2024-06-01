plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}

android {
    namespace = "com.monoid.hackernews.common.view"
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

    buildFeatures {
    }

    composeOptions {
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibsNio)

    api(project(":common:domain"))

    implementation(platform(libs.composeBom))
    implementation(platform(libs.koinBom))

    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidxCompose)
    lintChecks(libs.composeLintChecks)
    implementation(libs.bundles.google)

    testImplementation(libs.bundles.test)
}
