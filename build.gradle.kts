import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.googlePlayServices) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.firebasePerf) apply false
    alias(libs.plugins.versions) apply true
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA", "BETA", "RC").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"

            if (project.findProperty("hackernews.enableComposeCompilerReports") == "true") {
                // force tasks to rerun so that metrics are generated
                outputs.upToDateWhen { false }
                freeCompilerArgs = freeCompilerArgs +
                    "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.projectDir.absolutePath}/build/compose_metrics/" +
                    "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.projectDir.absolutePath}/build/compose_metrics/"
            }
        }
    }
}
