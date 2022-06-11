import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

apply(plugin = "com.github.ben-manes.versions")

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        // must import plugins in top level or kotlin compiler gets ir errors

        classpath(kotlin("gradle-plugin", version = "1.6.21"))
        classpath(kotlin("serialization", version = "1.6.21"))
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.18")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.42.0")
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            if (project.findProperty("hackernews.enableComposeCompilerReports") == "true") {
                freeCompilerArgs = freeCompilerArgs +
                    "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_metrics" +
                    "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_metrics"
            }
        }
    }
}
