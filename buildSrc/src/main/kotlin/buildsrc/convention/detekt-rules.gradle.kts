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
tasks.register<Detekt>("myDetekt") {
    description = "Runs a custom detekt build."
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
    debug = true
    setSource(
        files(
            "src/commonMain/kotlin",
            "src/commonTest/kotlin",
            "src/androidMain/kotlin",
            "src/androidTest/kotlin",
            "src/jvmMain/kotlin",
            "src/jvmTest/kotlin",
            "src/iosMain/kotlin",
            "src/iosTest/kotlin",
        )
    )
    include("**/*.kt")
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = libs.versions.jvmTarget.get()
}
