package com.monoid.hackernews.detekt.extensions.rules

import com.monoid.hackernews.detekt.rules.CatchingCoroutineCancellation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CatchingCoroutineCancellationLambdaSpec(private val env: KotlinCoreEnvironment) {
    private val subject = CatchingCoroutineCancellation()

    @Test
    fun `Catches `() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlin.coroutines.EmptyCoroutineContext
            fun main() {
                CoroutineScope(EmptyCoroutineContext).launch {
                    try {
                        println("Hello, World!")
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }
            }
        """.trimIndent()
        assert(subject.compileAndLintWithContext(env, code).isEmpty())
    }
}
