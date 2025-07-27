package buildsrc.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("io.gitlab.arturbosch.detekt")
}
val libs = the<LibrariesForLibs>()
detekt {
    buildUponDefaultConfig = false // preconfigure defaults.
    allRules = false // activate all available (even unstable) rules.
    autoCorrect = false // To enable or disable auto formatting.
    // To enable or disable parallel execution of detekt on multiple submodules.
    parallel = true
    // point to your custom config defining rules to run, overwriting default behavior.
    config.setFrom("${rootProject.projectDir}/detekt.yml")
    baseline = projectDir.resolve("detekt-baseline.xml")
}
dependencies {
    detektPlugins(libs.detektFormatting)
    detektPlugins(libs.robsRules)
}
tasks.withType<Detekt> {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    reports {
        // observe findings in your browser with structure and code snippets
        html {
            required = true
            outputLocation = file("build/reports/mydetekt.html")
        }
        // similar to the console output, contains issue signature to manually edit baseline files
        txt {
            required = true
            outputLocation = file("build/reports/mydetekt.txt")
        }
        // simple Markdown format
        md {
            required = true
            outputLocation = file("build/reports/mydetekt.md")
        }
    }
    jvmTarget = libs.versions.jvmTarget.get()
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    jvmTarget = libs.versions.jvmTarget.get()
    exclude {
        it.file.relativeTo(projectDir).startsWith("build")
    }
}
pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    tasks.withType<Detekt>().configureEach {
        exclude {
            it.file.relativeTo(projectDir).startsWith("build")
        }
    }
}
