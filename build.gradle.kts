import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinPowerAssert) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.kotlinxParcelize) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.googlePlayServices) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.firebasePerf) apply false
    alias(libs.plugins.detekt) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

subprojects {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    tasks.withType<KotlinCommonCompile>().configureEach {
        compilerOptions {
            // keeps coroutine variables
            // freeCompilerArgs.add("-Xdebug")
            // recovers coroutine stack traces
            // vm options ("-ea")
            // https://github.com/Anamorphosee/stacktrace-decoroutinator

            extraWarnings.set(true)
            allWarningsAsErrors = false

            freeCompilerArgs.addAll(
                listOf(
                    "-opt-in=kotlinx.coroutines.FlowPreview",
                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
                            rootDir.absolutePath + "/compose_compiler_config.conf",
                    "-Xexpect-actual-classes",
                ),
            )

            if (project.findProperty("hackernews.enableComposeCompilerReports") == "true") {
                // force tasks to rerun so that metrics are generated
                outputs.upToDateWhen { false }
                freeCompilerArgs.addAll(
                    listOf(
                        "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                                projectDir.absolutePath + "/build/compose_metrics/",
                        "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                                projectDir.absolutePath + "/build/compose_metrics/",
                    ),
                )
            }
        }
    }
}
