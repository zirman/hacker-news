package buildsrc.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("io.gitlab.arturbosch.detekt")
}
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
tasks.withType<Detekt>().configureEach {
    jvmTarget = JavaVersion.VERSION_17.toString()
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JavaVersion.VERSION_17.toString()
}
detekt {
    buildUponDefaultConfig = true // preconfigure defaults.
    allRules = false // activate all available (even unstable) rules.
    autoCorrect = false // To enable or disable auto formatting.
    // To enable or disable parallel execution of detekt on multiple submodules.
    parallel = true
    // point to your custom config defining rules to run, overwriting default behavior.
    config.setFrom("${rootProject.projectDir}/detekt.yml")
}
tasks.withType<Detekt>().configureEach {
    reports {
        // observe findings in your browser with structure and code snippets
        html.required.set(true)
        // similar to the console output, contains issue signature to manually edit baseline files
        txt.required.set(true)
        // simple Markdown format
        md.required.set(true)
    }
}
pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    tasks.withType<Detekt>().configureEach {
        exclude {
            it.file.relativeTo(projectDir).startsWith("build")
        }
    }
}
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting")
    detektPlugins(project(":detekt-rules"))
}
