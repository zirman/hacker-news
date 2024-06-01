plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}

android {
    namespace = "com.monoid.hackernews.common.domain"
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

    api(project(":common:injection"))
    api(project(":common:data"))

    implementation(platform(libs.composeBom))
    implementation(platform(libs.koinBom))

    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.androidxCompose)
    lintChecks(libs.composeLintChecks)
    // TODO: refactor so that this isn't a shared dependency for wear
    implementation(libs.bundles.androidxApp)
    implementation(libs.bundles.google)

    testImplementation(libs.bundles.test)
}
