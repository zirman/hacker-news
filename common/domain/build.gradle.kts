plugins {
    id("kmplibrary")
    alias(libs.plugins.kotlinxSerialization)
    id("hackernews.detekt")
}
kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktorClientAndroid)
            implementation(libs.collectionKtx)
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
        }
        jvmMain.dependencies {
            implementation(libs.ktorClientJava)
        }
        iosMain.dependencies {
            implementation(libs.ktorClientDarwin)
        }
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.koinBom))
            compileOnly(libs.koinCore)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.navigationCompose)
            implementation(libs.annotation)
            api(libs.koinAnnotations)
            api(project(":common:injection"))
            api(project(":common:data"))
        }
        commonTest.dependencies {
            //implementation(libs.bundles.test)
        }
    }
}
val packageNamespace = "com.monoid.hackernews.common.domain"
compose {
    resources {
        packageOfResClass = packageNamespace
    }
}
android {
    namespace = packageNamespace
}
