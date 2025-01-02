package buildsrc.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("io.gitlab.arturbosch.detekt")
}
val libs = the<LibrariesForLibs>()
detekt {
    buildUponDefaultConfig = true // preconfigure defaults.
    allRules = false // activate all available (even unstable) rules.
    autoCorrect = false // To enable or disable auto formatting.
    // To enable or disable parallel execution of detekt on multiple submodules.
    parallel = true
    // point to your custom config defining rules to run, overwriting default behavior.
    config.setFrom("${rootProject.projectDir}/detekt.yml")
}
dependencies {
    detektPlugins(libs.detektFormatting)
    detektPlugins(project(":detekt-rules"))
}
pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    tasks.withType<Detekt>().configureEach {
        exclude {
            it.file.relativeTo(projectDir).startsWith("build")
        }
    }
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = libs.versions.jvmTarget.get()
    reports {
        // observe findings in your browser with structure and code snippets
        html.required.set(true)
        // similar to the console output, contains issue signature to manually edit baseline files
        txt.required.set(true)
        // simple Markdown format
        md.required.set(true)
    }
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = libs.versions.jvmTarget.get()
}
