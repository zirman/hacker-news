import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        // Apply detekt plugin to module
        pluginManager.apply(libs.findPlugin("detekt").get().get().pluginId)
        // Configure jvmTarget for gradle task `detekt`
        tasks.withType<Detekt>().configureEach {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
        // Configure jvmTarget for gradle task `detektGenerateBaseline`
        tasks.withType<DetektCreateBaselineTask>().configureEach {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
        // Configure detekt
        extensions.getByType<DetektExtension>().apply {
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
            "detektPlugins"(libs.findLibrary("detektFormatting").get())
            "detektPlugins"(project(":detekt-rules"))
        }
    }
}
