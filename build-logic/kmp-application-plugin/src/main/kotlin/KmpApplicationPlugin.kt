@file:OptIn(ExperimentalComposeLibrary::class)

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.tasks.asJavaVersion
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.internal.sharedruntime.codegen.sourceNameOfBinaryName
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

class KmpApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        applyPlugin("kotlinMultiplatform")
        applyPlugin("androidApplication")
        applyPlugin("jetbrainsCompose")
        applyPlugin("composeCompiler")
        applyPlugin("ksp")
        val compose = extensions.getByType<ComposeExtension>().dependencies
        configureExtension<KotlinMultiplatformExtension> {
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
            jvmToolchain(getVersion("jvmToolchain"))
            androidTarget()
            jvm()
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64(),
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = "ComposeApp"
                    isStatic = true
                }
            }
            sourceSets.named("commonMain") {
                dependencies {
                    implementation(compose.animation)
                    implementation(compose.animationGraphics)
                    implementation(compose.components.resources)
                    implementation(compose.components.uiToolingPreview)
                    implementation(compose.desktop.common)
                    implementation(compose.desktop.components.animatedImage)
                    implementation(compose.desktop.components.splitPane)
                    implementation(compose.desktop.currentOs)
                    implementation(compose.foundation)
                    implementation(compose.material3)
                    implementation(compose.materialIconsExtended)
                    implementation(compose.material3AdaptiveNavigationSuite)
                    implementation(compose.preview)
                    implementation(compose.runtime)
                    implementation(compose.ui)
                    implementation(compose.uiTooling)
                    implementation(compose.uiUtil)
                }
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }
        configureExtension<BaseAppModuleExtension> {
            compileSdk = getVersion("compileSdk")
            compileSdkPreview = getVersionString("compileSdkPreview")
            buildToolsVersion = getVersionString("buildToolsVersion")
            defaultConfig {
                minSdk = getVersion("minSdk")
                targetSdk = getVersion("targetSdk")
            }
            compileOptions {
                isCoreLibraryDesugaringEnabled = true
                sourceCompatibility =
                    JavaLanguageVersion.of(getVersion("jvmTarget")).asJavaVersion()
                targetCompatibility =
                    JavaLanguageVersion.of(getVersion("jvmTarget")).asJavaVersion()
            }
            sourceNameOfBinaryName("main")
            sourceSets.named("main").get().apply {
                manifest.srcFile("src/androidMain/AndroidManifest.xml")
                res.srcDirs("src/androidMain/res")
                resources.srcDirs("src/commonMain/resources")
            }
            packaging {
                resources {
                    excludes += "/META-INF/versions/9/previous-compilation-data.bin"
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
            buildTypes {
                debug {
                    applicationIdSuffix = ".debug"
                }
            }
        }
        dependencies {
            "coreLibraryDesugaring"(findLibrary("desugarJdkLibsNio"))
            "kspCommonMainMetadata"(findLibrary("koinKspCompiler"))
            "kspAndroid"(findLibrary("koinKspCompiler"))
            "kspJvm"(findLibrary("koinKspCompiler"))
            "kspIosX64"(findLibrary("koinKspCompiler"))
            "kspIosArm64"(findLibrary("koinKspCompiler"))
            "kspIosSimulatorArm64"(findLibrary("koinKspCompiler"))
        }
        configureExtension<KspExtension> {
            arg("KOIN_CONFIG_CHECK", "true")
            arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
        }
        configureExtension<ComposeExtension> {
            checkNotNull(extensions.getByType(ResourcesExtension::class.java)).apply {
                publicResClass = true
                generateResClass = always
            }
        }
        // Trigger Common Metadata Generation from Native tasks
        tasks.withType(KotlinCompilationTask::class.java).configureEach {
            if (name != "kspCommonMainKotlinMetadata") {
                dependsOn("kspCommonMainKotlinMetadata")
            }
        }
    }
}

internal inline fun <reified T : Any> Project.configureExtension(block: T.() -> Unit) {
    checkNotNull(extensions.findByType<T>()).run(block)
}

internal fun VersionCatalog.getVersion(alias: String): Int =
    findVersion(alias).get().requiredVersion.toInt()

internal fun Project.getVersion(alias: String): Int = libs.getVersion(alias)
internal fun Project.getVersionString(alias: String): String = libs.getVersionString(alias)
internal fun VersionCatalog.getVersionString(alias: String): String {
    return findVersion(alias).get().requiredVersion
}

internal fun Project.applyPlugin(alias: String) = pluginManager.apply(findPlugin(alias).pluginId)

internal val Project.libs: VersionCatalog
    get() {
        return extensions.getByType<VersionCatalogsExtension>().named("libs")
    }

internal fun Project.findLibrary(alias: String) = libs.findLibrary(alias).get()

internal fun Project.findPlugin(alias: String): PluginDependency =
    libs.findPlugin(alias).get().get()
