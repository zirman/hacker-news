package com.monoid.hackernews.detekt.extensions.rules

import com.monoid.hackernews.detekt.rules.CatchingCoroutineCancellationLambda
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CatchingCoroutineCancellationLambdaSpec(private val env: KotlinCoreEnvironment) {
    private val subject = CatchingCoroutineCancellationLambda()

    @Test
    fun `Report catching coroutine cancellation in named suspend fun`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun main() {
                try {
                    delay(1_000)
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        assert(subject.compileAndLintWithContext(env, code).size == 1)
    }

    @Test
    fun `Report catching coroutine cancellation in launch coroutine builder`() {
        env.configuration
        val code = """
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch

            fun test() {
                GlobalScope.launch {
                    try {
                        delay(1_000)
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
            }
        """.trimIndent()
        assert(subject.compileAndLintWithContext(env, code).size == 1)
    }

    @Test
    fun `Allows catching Throwable in named suspend fun that calls ensureActive()`() {
        val code = """            
            suspend fun main() {
                try {
                    println("Hello, World!")
                } catch (throwable: Throwable) {
                    currentCoroutineContext().ensureActive()
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        assert(subject.compileAndLintWithContext(env, code).size == 1)
    }

    @Test
    fun `Allows catching IllegalStateException in named suspend fun without calling ensureActive()`() {
        val code = """            
            suspend fun main() {
                try {
                    println("Hello, World!")
                } catch (throwable: IllegalStateException) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()
        assert(subject.compileAndLintWithContext(env, code).isEmpty())
    }
}
