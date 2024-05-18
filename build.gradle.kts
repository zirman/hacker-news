import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.kotlinxParcelize) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.googlePlayServices) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.firebasePerf) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            allWarningsAsErrors = false

            jvmTarget = libs.versions.jvmTarget.get()

            freeCompilerArgs += listOf(
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=${project.rootDir}/compose_compiler_config.conf",
            )

            if (project.findProperty("hackernews.enableComposeCompilerReports") == "true") {
                // force tasks to rerun so that metrics are generated
                outputs.upToDateWhen { false }
                freeCompilerArgs += listOf(
                    "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.projectDir.absolutePath}/build/compose_metrics/",
                    "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.projectDir.absolutePath}/build/compose_metrics/",
                )
            }
        }
    }
}
