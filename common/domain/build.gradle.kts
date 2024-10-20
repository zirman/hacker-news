plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.composeCompiler)
    id("hackernews.detekt")
}
kotlin {
    compilerOptions {
        extraWarnings.set(true)
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    jvm { }
    sourceSets {
        jvmMain.dependencies {}
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.kotilnxCoroutinesBom))
            implementation(project.dependencies.platform(libs.kotlinWrappersBom))
            implementation(project.dependencies.platform(libs.koinBom))
            compileOnly(libs.koinCore)
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.kotlinx)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
            implementation(libs.navigationCompose)
            implementation(libs.annotation)
            implementation(libs.collectionKtx)
            api(project(":common:injection"))
            api(project(":common:data"))
        }
        commonTest.dependencies {
            implementation(libs.bundles.test)
        }
    }
}
