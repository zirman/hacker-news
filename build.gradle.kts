import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

apply(plugin = "com.github.ben-manes.versions")

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = libs.versions.gradle.plugin.kotlin.get()))
        classpath(kotlin("serialization", version = libs.versions.gradle.plugin.kotlin.get()))
        classpath("com.android.tools.build:gradle:${libs.versions.gradle.plugin.android.get()}")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${libs.versions.gradle.plugin.ksp.get()}")
        classpath("com.google.protobuf:protobuf-gradle-plugin:${libs.versions.gradle.plugin.protobuf.get()}")
        classpath("com.github.ben-manes:gradle-versions-plugin:${libs.versions.gradle.plugin.versions.get()}")
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
