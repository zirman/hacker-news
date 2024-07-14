package com.monoid.hackernews.detekt.extensions.rules

import com.monoid.hackernews.detekt.rules.CatchingCoroutineCancellation
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

class CatchingCoroutineCancellationSpec {
    private val subject = CatchingCoroutineCancellation()

    @Test
    fun `Passes Throwable in non-suspend fun`() {
        val code = """
            fun main() {
                try {
                    println("Hello, World!")        
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()

        assert(subject.lint(code).isEmpty())
    }

    @Test
    fun `Catches Throwable in suspend fun`() {
        val code = """
            suspend fun main() {
                try {
                    println("Hello, World!")        
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        """.trimIndent()

        assert(subject.lint(code).size == 1)
    }

    @Test
    fun `Catches Exception in suspend fun`() {
        val code = """
            suspend fun main() {
                try {
                    println("Hello, World!")        
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        """.trimIndent()

        assert(subject.lint(code).size == 1)
    }

    @Test
    fun `Catches CancellationException in suspend fun`() {
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

        assert(subject.lint(code).size == 1)
    }
}
