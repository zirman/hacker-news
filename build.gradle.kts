buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        // must import plugins in top level or kotlin compiler gets ir errors
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.20")
        classpath("com.google.protobuf:protobuf-gradle-plugin:0.8.18")
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
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
