package com.monoid.hackernews.detekt.extensions.rules

import com.monoid.hackernews.detekt.rules.CatchingCoroutineCancellation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CatchingCoroutineCancellationSpec(private val env: KotlinCoreEnvironment) {

    @Test
    fun `Detect caught Throwable in suspend fun`() {
        val code = """
            suspend fun main() {
                try {
                    println("Hello, World!")
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Pass caught Throwable in non suspend fun`() {
        val code = """
            fun main() {
                try {
                    println("Hello, World!")
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Detect caught Exception in suspend fun`() {
        val code = """
            suspend fun main() {
                try {
                    println("Hello, World!")
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Pass caught Exception in non suspend fun`() {
        val code = """
            fun main() {
                try {
                    println("Hello, World!")
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Detect caught CancellationException in suspend fun`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            suspend fun main() {
                try {
                    println("Hello, World!")
                } catch (cancellationException: CancellationException) {
                    cancellationException.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Pass caught CancellationException in non suspend fun`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            fun main() {
                try {
                    println("Hello, World!")
                } catch (cancellationException: CancellationException) {
                    cancellationException.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Pass caught CancellationException in nested lambda non suspend fun`() {
        val code = """
            suspend fun main() {
                fun foo() {
                    try {
                        print("Hello, ")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
                foo()
                println("World!")
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Detect caught CancellationException in inline lambda non suspend lambda`() {
        val code = """
            suspend fun main() {
                run {
                    try {
                        println("Hello, World")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Pass caught IllegalStateException in suspend fun`() {
        val code = """
            suspend fun main() {
                try {
                    println("Hello, World")
                } catch (illegalStateException: IllegalStateException) {
                    illegalStateException.printStackTrace()
                }
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Detekt caught throwable in suspend lambda`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            fun main() {
                GlobalScope.launch {
                    try {
                        println("Hello, World")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }                
            }
        """.trimIndent()
        val findings = CatchingCoroutineCancellation().compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }
}
