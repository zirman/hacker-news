plugins {
    id("kmplibrary")
    id("hackernews.detekt")
}
kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koinLoggerSlf4j)
            implementation(libs.koinKtor)
            implementation(libs.ktorClientAndroid)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(libs.bundles.firebase)
            implementation(libs.kotlinxCoroutinesAndroid)
        }
        jvmMain.dependencies {
            implementation(libs.ktorClientJava)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            compileOnly(libs.koinCore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            api(libs.koinAnnotations)
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.koinBom))
            implementation(project.dependencies.platform(libs.firebaseBom))
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
    }
}
val packageNamespace = "com.monoid.hackernews.common.injection"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
}
