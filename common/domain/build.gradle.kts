import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    id("hackernews.detekt")
}
kotlin {
    compilerOptions {
        extraWarnings.set(true)
    }
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
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
            api(libs.koinAnnotations)
            api(project(":common:injection"))
            api(project(":common:data"))
        }
        commonTest.dependencies {
            implementation(libs.bundles.test)
        }
        sourceSets.named("commonMain") {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}
dependencies {
    add("kspCommonMainMetadata", libs.koinKspCompiler)
    add("kspJvm", libs.koinKspCompiler)
}
ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
// Trigger Common Metadata Generation from Native tasks
//project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
//    if(name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}
