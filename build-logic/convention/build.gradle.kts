import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.monoid.hackernews.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.detektGradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin { // Create this block if it doesn't exist
    plugins {
        register("detekt") { // A unique string
            // This is the name we'll be using in all our modules to apply this plugin.
            id = "hackernews.detekt"
            // Class name of the plugin file which implements `Plugin` interface.
            implementationClass = "DetektConventionPlugin"
        }
    }
}
