//subprojects {
//    tasks.withType<KotlinJvmCompile>().configureEach {
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_17)
//        }
//    }
//    tasks.withType<KotlinCommonCompile>().configureEach {
//        compilerOptions {
            // keeps coroutine variables
            // freeCompilerArgs.add("-Xdebug")
            // recovers coroutine stack traces
            // vm options ("-ea")
            // https://github.com/Anamorphosee/stacktrace-decoroutinator

//            extraWarnings.set(true)
//            allWarningsAsErrors = false
//
//            freeCompilerArgs.addAll(
//                listOf(
//                    "-opt-in=kotlinx.coroutines.FlowPreview",
//                    "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
//                    "-P",
//                    "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
//                            rootDir.absolutePath + "/compose_compiler_config.conf",
//                ),
//            )
//
//            if (project.findProperty("hackernews.enableComposeCompilerReports") == "true") {
//                // force tasks to rerun so that metrics are generated
//                outputs.upToDateWhen { false }
//                freeCompilerArgs.addAll(
//                    listOf(
//                        "-P=plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
//                                projectDir.absolutePath + "/build/compose_metrics/",
//                        "-P=plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
//                                projectDir.absolutePath + "/build/compose_metrics/",
//                    ),
//                )
//            }
//        }
//    }
//}
