plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxParcelize)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.protobuf)
    alias(libs.plugins.room)
}

kotlin {
    jvm {
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = libs.versions.jvmTarget.get()
            }
        }
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

            // Lower-level APIs with support for custom serialization
//            implementation(libs.datastoreCoreOkio)
            // Higher-level APIs for storing values of basic types
//            implementation(libs.datastorePreferencesCore)
//            project.dependencies.annotationProcessor(libs.roomCompiler)

            implementation(project(":common:injection"))
        }

        commonTest.dependencies {
            implementation(libs.kotlinTest)
        }

        androidMain.dependencies {
            project.dependencies.coreLibraryDesugaring(libs.desugarJdkLibsNio)
//            project.dependencies.annotationProcessor(libs.roomCompiler)

//    implementation(project(":common:injection"))
//            implementation(project.dependencies.platform(libs.composeBom))
//            implementation(project.dependencies.platform(libs.koinBom))


//    ksp(libs.roomCompiler)
//
//    testImplementation(libs.bundles.test)
        }
    }
}

dependencies {
    add("kspAndroid", libs.roomCompiler)
}

android {
    namespace = "com.monoid.hackernews.common.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug { }
        release { }
    }

    compileOptions {
//        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

//    kotlinOptions {
//        jvmTarget = libs.versions.jvmTarget.get()
//    }

    buildFeatures {
        buildConfig = true
//        compose = true
    }
//
//    composeOptions {
//        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
//    }
}

//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
//    }
//
//    plugins {
//        id("java")
//        id("kotlin")
//    }
//
//    generateProtoTasks {
//        all().forEach { task ->
//            task.builtins {
//                kotlin {}
//                java {}
//            }
//
//            task.plugins {
//                id("java") {
//                    option("lite")
//                }
//
//                id("kotlin") {
//                    option("lite")
//                }
//            }
//        }
//    }
//}

//androidComponents {
//    onVariants(selector().all()) { variant ->
//        afterEvaluate {
//            val capName = variant.name.capitalized()
//            tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
//                setSource(tasks.getByName("generate${capName}Proto").outputs)
//            }
//        }
//    }
//}

//dependencies {
//    coreLibraryDesugaring(libs.desugarJdkLibsNio)
//
//    implementation(project(":common:injection"))
//
//    implementation(platform(libs.composeBom))
//    implementation(platform(libs.koinBom))
//
//    implementation(libs.bundles.kotlinx)
//    implementation(libs.bundles.koin)
//    implementation(libs.bundles.androidx)
//    implementation(libs.bundles.google)
//    api(libs.datastore)
//
//    api(libs.bundles.ktor)
//    ksp(libs.roomCompiler)
//
//    testImplementation(libs.bundles.test)
//}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

room {
    schemaDirectory("$projectDir/schemas")
}
