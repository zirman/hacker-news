import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

plugins {
    id("dev.detekt")
}
val libs = the<VersionCatalogsExtension>().named("libs")
detekt {
    toolVersion = "2.0.0-alpha.2"
    buildUponDefaultConfig = true // false // preconfigure defaults.
    allRules = false // activate all available (even unstable) rules.
    autoCorrect = true // To enable or disable auto formatting.
    // To enable or disable parallel execution of detekt on multiple submodules.
    parallel = true
    // point to your custom config defining rules to run, overwriting default behavior.
//    config.setFrom("${rootProject.projectDir}/detekt.yml")
    baseline = projectDir.resolve("detekt-baseline.xml")
    enableCompilerPlugin = false
    // verbose output
    debug = true
    ignoredBuildTypes = listOf("release")
}
dependencies {
    detektPlugins(libs.findLibrary("ktlint").get())
}
tasks.withType<Detekt> {
//    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    reports {
        // observe findings in your browser with structure and code snippets
        html {
            required = true
            outputLocation = file("build/reports/mydetekt.html")
        }
        // simple Markdown format
        markdown {
            required = true
            outputLocation = file("build/reports/mydetekt.md")
        }
    }
    jvmTarget = libs.findVersion("jvmTarget").get().requiredVersion
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    jvmTarget = libs.findVersion("jvmTarget").get().requiredVersion
    exclude {
        it.file.relativeTo(projectDir).startsWith("build")
    }
}
//pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
//    tasks.withType<Detekt>().configureEach {
//        exclude {
//            it.file.relativeTo(projectDir).startsWith("build")
//        }
//    }
//}
