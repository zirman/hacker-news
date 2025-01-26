package com.monoid.hackernews.detekt.extensions.rules

import com.monoid.hackernews.detekt.rules.RunCatchingCoroutineCancellation
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("LargeClass")
@KotlinCoreEnvironmentTest
class RunCatchingCoroutineCancellationSpec(private val env: KotlinCoreEnvironment) {

    private val subject = RunCatchingCoroutineCancellation(Config.empty)

    @Test
    fun `Report suspend function call in runCatching`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 5)),
            listOf(SourceLocation(3, 16)),
        )
    }

    @Test
    fun `Report suspend function call in runCatching when not immediately checked in chain`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching { delay(1_000) }
                    .map { println(it) }
                    .onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 5)),
            listOf(SourceLocation(3, 16)),
        )
    }

    @Test
    fun `Report suspend function call in recoverCatching`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() {
                runCatching {
                    println("Hello")
                }.recoverCatching {
                    delay(1_000)
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(5, 7)),
            listOf(SourceLocation(5, 22)),
        )
    }

    @Test
    fun `Allow suspend function call in recoverCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    println("Hello")
                }.recoverCatching {
                    delay(1_000)
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report suspend function call in mapCatching`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() {
                runCatching {
                    println("Hello")
                }.mapCatching {
                    delay(1_000)
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(5, 7)),
            listOf(SourceLocation(5, 18)),
        )
    }

    @Test
    fun `Allow suspend function call in mapCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    println("Hello")
                }.mapCatching {
                    delay(1_000)
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report suspend function call in runCatching when ensureActive is second expression in onFailure`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                }.onFailure {
                    delay(1_000)
                    coroutineContext.ensureActive()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 5)),
            listOf(SourceLocation(3, 16)),
        )
    }

    @Test
    fun `Allow suspend function call in runCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Allow suspend function call in runCatching with parenthesis`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                }.onFailure({ currentCoroutineContext().ensureActive() })
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Allow suspend function call in runCatching with named argument`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                }.onFailure(action = { currentCoroutineContext().ensureActive() })
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report for in case of nested runCatching`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(2_000)
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                    runCatching { 
                        bar()
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5), SourceLocation(6, 9)),
            listOf(SourceLocation(4, 16), SourceLocation(6, 20)),
        )
    }

    @Test
    fun `Allow for in case of nested runCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(2_000)
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                    runCatching { 
                        bar()
                    }
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report for in case of nested runCatching with suspend fun call in inner runCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(2_000)
            suspend fun foo() {
                runCatching {
                    MainScope().launch { 
                        delay(1_000)
                        runCatching { 
                            bar()
                        }
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(7, 13)),
            listOf(SourceLocation(7, 24)),
        )
    }

    @Test
    fun `Allow for in case of nested runCatching with suspend fun call in inner runCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(2_000)
            suspend fun foo() {
                runCatching {
                    MainScope().launch { 
                        delay(1_000)
                        runCatching { 
                            bar()
                        }.onFailure { currentCoroutineContext().ensureActive() }
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report for in case of nested runCatching with suspend fun call in outer runCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(2_000)
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                    runCatching {
                        MainScope().launch { 
                            bar()
                        }
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow for in case of nested runCatching with suspend fun call in outer runCatching`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(2_000)
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                    runCatching {
                        MainScope().launch { 
                            bar()
                        }
                    }
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report for delay() in suspend functions`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 5)),
            listOf(SourceLocation(3, 16)),
        )
    }

    @Test
    fun `Allow for delay() in suspend functions`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    delay(1_000)
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report for coroutineContext in suspend functions`() {
        val code = """
            import kotlinx.coroutines.delay
            import kotlin.coroutines.coroutineContext
            suspend fun foo() {
                runCatching {
                    coroutineContext    
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow for coroutineContext in suspend functions`() {
        val code = """
            import kotlinx.coroutines.*
            import kotlin.coroutines.coroutineContext
            suspend fun foo() {
                runCatching {
                    coroutineContext    
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Allow no suspending function is used inside runBlocking`() {
        val code = """
            @Suppress("RedundantSuspendModifier")
            suspend fun foo() {
                runCatching {
                    Thread.sleep(1_000)
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report when _when_ is used in result`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1_000)
            suspend fun foo(): Result<*> {
                val result = runCatching { bar() }
                when(result.isSuccess) {
                    true -> TODO()
                    false -> TODO()
                }
                return result
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 18)),
            listOf(SourceLocation(4, 29)),
        )
    }

    @Test
    fun `Allow when _when_ is used in result`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(1_000)
            suspend fun foo(): Result<*> {
                val result = runCatching { bar() }.onFailure { currentCoroutineContext().ensureActive() }
                when(result.isSuccess) {
                    true -> TODO()
                    false -> TODO()
                }
                return result
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report when onSuccess is used in result`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1_000)
            suspend fun foo() {
                runCatching { bar() }.onSuccess { 
                    TODO()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow when onSuccess is used in result`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(1_000)
            suspend fun foo() {
                runCatching { bar() }.onFailure { currentCoroutineContext().ensureActive() }.onSuccess { 
                    TODO()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report when runCatching is used as function expression`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1_000)
            suspend fun foo() = runCatching { bar() }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 21)),
            listOf(SourceLocation(3, 32)),
        )
    }

    @Test
    fun `Allow when runCatching is used as function expression`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(1_000)
            suspend fun foo() = runCatching { bar() }.onFailure { currentCoroutineContext().ensureActive() }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Allow when try catch is used`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() {
                try {
                    delay(1_000)
                } catch (e: IllegalStateException) {
                    // handle error
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Nested
    inner class WithLambda {

        @Test
        fun `Report when suspend fun is called inside inline function`() {
            val code = """
                import kotlinx.coroutines.delay
                suspend fun foo() {
                    runCatching {
                        listOf(1L, 2L, 3L).map {
                            delay(it)
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(3, 5)),
                listOf(SourceLocation(3, 16)),
            )
        }

        @Test
        fun `Allow when suspend fun is called inside inline function`() {
            val code = """
                import kotlinx.coroutines.*
                suspend fun foo() {
                    runCatching {
                        listOf(1L, 2L, 3L).map {
                            delay(it)
                        }
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Allow when lambda in non suspending inline function is passed as crossinline`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun <R> foo(crossinline block: suspend () -> R) = MainScope().launch {
                    block()
                }
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1_000)
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report when lambda in suspend inline function is passed as crossinline`() {
            val code = """
                import kotlinx.coroutines.*
                suspend inline fun <R> foo(crossinline block: suspend () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1_000)
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(4, 5)),
                listOf(SourceLocation(4, 16)),
            )
        }

        @Test
        fun `Allow when lambda in suspend inline function is passed as crossinline`() {
            val code = """
                import kotlinx.coroutines.*
                suspend inline fun <R> foo(crossinline block: suspend () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1_000)
                        }
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Allow when lambda parameter chain has noinline function call`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun <R> inline(block: () -> R) = block()
                fun <R> noInline(block: suspend () -> R) = MainScope().launch { block() }
                suspend fun bar() {
                    runCatching {
                        inline {
                            noInline {
                                inline {
                                    delay(1_000)
                                }
                            }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report when lambda parameter chain has all inlined function call`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun <R> inline(block: () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        inline {
                            inline {
                                delay(1_000)
                            }
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(4, 5)),
                listOf(SourceLocation(4, 16)),
            )
        }

        @Test
        fun `Allow when lambda parameter chain has all inlined function call`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun <R> inline(block: () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        inline {
                            inline {
                                delay(1_000)
                            }
                        }
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Allow when lambda in non suspending inline function is passed as noinline`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun <R> foo(noinline block: suspend () -> R) = MainScope().launch {
                    block()
                }
                suspend fun suspendFun() = delay(1_000)
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1_000)
                        }
                        val baz = suspend {
                            delay(1_000)
                        }
                        foo(baz)
                        foo(::suspendFun)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report when lambda in suspend inline function is passed as noinline`() {
            val code = """
                import kotlinx.coroutines.delay
                suspend inline fun <R> foo(noinline block: suspend () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1_000)
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(4, 5)),
                listOf(SourceLocation(4, 16)),
            )
        }

        @Test
        fun `Allow when lambda in suspend inline function is passed as noinline`() {
            val code = """
                import kotlinx.coroutines.*
                suspend inline fun <R> foo(noinline block: suspend () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1_000)
                        }
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report when suspend fun is called as extension function`() {
            val code = """
                import kotlinx.coroutines.delay
                @Suppress("RedundantSuspendModifier")
                suspend fun List<Long>.await() = delay(this.size.toLong())
                suspend fun foo() {
                    runCatching {
                        listOf(1L, 2L, 3L).await()
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(5, 5)),
                listOf(SourceLocation(5, 16)),
            )
        }

        @Test
        fun `Allow when suspend fun is called as extension function`() {
            val code = """
                import kotlinx.coroutines.*
                @Suppress("RedundantSuspendModifier")
                suspend fun List<Long>.await() = delay(this.size.toLong())
                suspend fun foo() {
                    runCatching {
                        listOf(1L, 2L, 3L).await()
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report when inside inline function with noinline and cross inline parameters in same order`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun foo(
                    noinline noinlineBlock: suspend () -> Unit,
                    inlineBlock: () -> Unit,
                    crossinline crossinlineBlock: suspend () -> Unit,
                ) = inlineBlock().toString() + MainScope().launch {
                    noinlineBlock()
                } + runBlocking {
                    crossinlineBlock()
                }.toString()
                suspend fun bar() {
                    runCatching {
                        foo(
                            noinlineBlock = {
                                delay(2_000)
                            },
                            inlineBlock = { delay(1_000) },
                        ) {

                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(12, 5)),
                listOf(SourceLocation(12, 16)),
            )
        }

        @Test
        fun `Allow when inside inline function with noinline and cross inline parameters in same order`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun foo(
                    noinline noinlineBlock: suspend () -> Unit,
                    inlineBlock: () -> Unit,
                    crossinline crossinlineBlock: suspend () -> Unit,
                ) = inlineBlock().toString() + MainScope().launch {
                    noinlineBlock()
                } + runBlocking {
                    crossinlineBlock()
                }.toString()
                suspend fun bar() {
                    runCatching {
                        foo(
                            noinlineBlock = {
                                delay(2_000)
                            },
                            inlineBlock = { delay(1_000) },
                        ) {

                        }
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report when inside inline function with noinline and cross inline parameters not in same order`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun foo(
                    noinline noinlineBlock: suspend () -> Unit,
                    inlineBlock: () -> Unit,
                    crossinline crossinlineBlock: suspend () -> Unit,
                ) = inlineBlock().toString() + MainScope().launch {
                    noinlineBlock()
                } + runBlocking {
                    crossinlineBlock()
                }.toString()
                suspend fun bar() {
                    runCatching {
                        foo(
                            inlineBlock = { delay(1_000) },
                            noinlineBlock = {
                                delay(2_000)
                            },
                        ) {
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(12, 5)),
                listOf(SourceLocation(12, 16)),
            )
        }

        @Test
        fun `Allow when inside inline function with noinline and cross inline parameters not in same order`() {
            val code = """
                import kotlinx.coroutines.*
                inline fun foo(
                    noinline noinlineBlock: suspend () -> Unit,
                    inlineBlock: () -> Unit,
                    crossinline crossinlineBlock: suspend () -> Unit,
                ) = inlineBlock().toString() + MainScope().launch {
                    noinlineBlock()
                } + runBlocking {
                    crossinlineBlock()
                }.toString()
                suspend fun bar() {
                    runCatching {
                        foo(
                            inlineBlock = { delay(1_000) },
                            noinlineBlock = {
                                delay(2_000)
                            },
                        ) {

                        }
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Test
    fun `Report when suspend fun in for subject is used`() {
        val code = """
            @Suppress("RedundantSuspendModifier")
            suspend fun bar() = 10
            suspend fun foo() {
                runCatching {
                    for (i in 1..bar()) {
                        println(i)
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow when suspend fun in for subject is used`() {
        val code = """
            import kotlinx.coroutines.*
            @Suppress("RedundantSuspendModifier")
            suspend fun bar() = 10
            suspend fun foo() {
                runCatching {
                    for (i in 1..bar()) {
                        println(i)
                    }
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report in case of suspendCancellableCoroutine`() {
        val code = """
            import kotlinx.coroutines.*
            import kotlin.coroutines.resume
            suspend fun foo() {
                runCatching {
                    suspendCancellableCoroutine {
                        it.resume(Unit)
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow in case of suspendCancellableCoroutine`() {
        val code = """
            import kotlinx.coroutines.*
            import kotlin.coroutines.resume
            suspend fun foo() {
                runCatching {
                    suspendCancellableCoroutine {
                        it.resume(Unit)
                    }
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report in case suspend callable reference is invoked`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1_000)
            suspend fun foo() {
                runCatching {
                    ::bar.invoke()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow in case suspend callable reference is invoked`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun bar() = delay(1_000)
            suspend fun foo() {
                runCatching {
                    ::bar.invoke()
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report in case suspend local function is invoked`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() {
                suspend fun localFun() = delay(1_000)
                runCatching {
                    localFun()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow in case suspend local function is invoked`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                suspend fun localFun() = delay(1_000)
                runCatching {
                    localFun()
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Allow coroutine is launched`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    MainScope().launch {
                        delay(1_000)
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report when job is joined`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    MainScope().launch {
                        delay(1_000)
                    }.join()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 5)),
            listOf(SourceLocation(3, 16)),
        )
    }

    @Test
    fun `Allow when job is joined`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    MainScope().launch {
                        delay(1_000)
                    }.join()
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Allow async is used`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    MainScope().async {
                        delay(1_000)
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report async is awaited`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    @Suppress("RedundantAsync")
                    MainScope().async {
                        delay(1_000)
                    }.await()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 5)),
            listOf(SourceLocation(3, 16)),
        )
    }

    @Test
    fun `Allow async is awaited`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    @Suppress("RedundantAsync")
                    MainScope().async {
                        delay(1_000)
                    }.await()
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Allow when suspend fun is called inside runBlocking`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                runCatching {
                    runBlocking {
                        delay(1_000)
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report when suspend fun is called in string interpolation`() {
        val code = """
            import kotlinx.coroutines.delay
            @Suppress("RedundantSuspendModifier")
            suspend fun foo() {
                runCatching {
                    val string = "${'$'}{delay(1_000)}"
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16)),
        )
    }

    @Test
    fun `Allow when suspend fun is called in string interpolation`() {
        val code = """
            import kotlinx.coroutines.*
            @Suppress("RedundantSuspendModifier")
            suspend fun foo() {
                runCatching {
                    val string = "${'$'}{delay(1_000)}"
                }.onFailure { currentCoroutineContext().ensureActive() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Nested
    inner class WithOperators {

        @Test
        fun `Report in case of suspend invoked operator`() {
            val code = """
                import kotlinx.coroutines.delay
                class C {
                    suspend operator fun invoke() = delay(1_000)
                }
                suspend fun foo() {
                    runCatching {
                        C()()
                        C().invoke()
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend invoked operator`() {
            val code = """
                import kotlinx.coroutines.*
                class C {
                    suspend operator fun invoke() = delay(1_000)
                }
                suspend fun foo() {
                    runCatching {
                        C()()
                        C().invoke()
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend inc and dec operator`() {
            val code = """
                @Suppress("RedundantSuspendModifier")
                class OperatorClass {
                    suspend operator fun inc(): OperatorClass = OperatorClass()
                    suspend operator fun dec(): OperatorClass = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        var operatorClass = OperatorClass()
                        operatorClass++
                    }
                    runCatching {
                        var operatorClass = OperatorClass()
                        operatorClass--
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(11, 5), SourceLocation(7, 5)),
                listOf(SourceLocation(11, 16), SourceLocation(7, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend inc and dec operator`() {
            val code = """
                import kotlinx.coroutines.*
                @Suppress("RedundantSuspendModifier")
                class OperatorClass {
                    suspend operator fun inc(): OperatorClass = OperatorClass()
                    suspend operator fun dec(): OperatorClass = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        var operatorClass = OperatorClass()
                        operatorClass++
                    }.onFailure { currentCoroutineContext().ensureActive() }
                    runCatching {
                        var operatorClass = OperatorClass()
                        operatorClass--
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend inc and dec operators called as function`() {
            val code = """
                @Suppress("RedundantSuspendModifier")
                class OperatorClass {
                    suspend operator fun inc(): OperatorClass = OperatorClass()
                    suspend operator fun dec(): OperatorClass = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        operatorClass.inc()
                    }
                    runCatching {
                        val operatorClass = OperatorClass()
                        operatorClass.dec()
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5), SourceLocation(11, 5)),
                listOf(SourceLocation(7, 16), SourceLocation(11, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend inc and dec operators called as function`() {
            val code = """
                import kotlinx.coroutines.*
                @Suppress("RedundantSuspendModifier")
                class OperatorClass {
                    suspend operator fun inc(): OperatorClass = OperatorClass()
                    suspend operator fun dec(): OperatorClass = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        operatorClass.inc()
                    }.onFailure { currentCoroutineContext().ensureActive() }
                    runCatching {
                        val operatorClass = OperatorClass()
                        operatorClass.dec()
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend not operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun not() = false
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(!operatorClass)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend not operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun not() = false
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(!operatorClass)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend unaryPlus operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun unaryPlus() = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(+operatorClass)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend unaryPlus operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun unaryPlus() = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(+operatorClass)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend plus operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun plus(test: OperatorClass) = test
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 + operatorClass2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend plus operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun plus(test: OperatorClass) = test
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 + operatorClass2)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend div operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun div(value: Int) = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(operatorClass / 2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend div operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun div(value: Int) = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(operatorClass / 2)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend compareTo operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun compareTo(operatorClass: OperatorClass) = 0
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 < operatorClass2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend compareTo operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun compareTo(operatorClass: OperatorClass) = 0
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 < operatorClass2)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend plusAssign operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun plusAssign(operatorClass: OperatorClass) {}
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        operatorClass1 += (operatorClass2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend plusAssign operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun plusAssign(operatorClass: OperatorClass) {}
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        operatorClass1 += (operatorClass2)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend plus called as plusAssign operator`() {
            val code = """
                class C
                @Suppress("RedundantSuspendModifier")
                suspend operator fun C.plus(i: Int): C = TODO()
                suspend fun f() {
                    runCatching {
                        var x = C()
                        x += 1
                    }
                    
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(5, 5)),
                listOf(SourceLocation(5, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend plus called as plusAssign operator`() {
            val code = """
                import kotlinx.coroutines.*
                class C
                @Suppress("RedundantSuspendModifier")
                suspend operator fun C.plus(i: Int): C = TODO()
                suspend fun f() {
                    runCatching {
                        var x = C()
                        x += 1
                    }.onFailure { currentCoroutineContext().ensureActive() }
                    
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend divAssign operator`() {
            val code = """
                import kotlinx.coroutines.delay
                class OperatorClass {
                    suspend operator fun divAssign(operatorClass: OperatorClass) {
                        delay(1_000)
                    }
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        operatorClass1 /= (operatorClass2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(8, 5)),
                listOf(SourceLocation(8, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend divAssign operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    suspend operator fun divAssign(operatorClass: OperatorClass) {
                        delay(1_000)
                    }
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        operatorClass1 /= (operatorClass2)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend rangeTo operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun rangeTo(operatorClass: OperatorClass) = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1..operatorClass2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend rangeTo operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun rangeTo(operatorClass: OperatorClass) = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1..operatorClass2)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Report in case of suspend times operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun times(operatorClass: OperatorClass) = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 * operatorClass2)
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16)),
            )
        }

        @Test
        fun `Allow in case of suspend times operator`() {
            val code = """
                import kotlinx.coroutines.*
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun times(operatorClass: OperatorClass) = OperatorClass()
                }
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 * operatorClass2)
                    }.onFailure { currentCoroutineContext().ensureActive() }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Nested
        inner class WithSuspendingIterator {

            @Test
            fun `Report when suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.delay
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    suspend fun bar() {
                        runCatching { 
                            for (x in SuspendingIterator()) {
                                println(x)
                            }
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(6, 5)),
                    listOf(SourceLocation(6, 16)),
                )
            }

            @Test
            fun `Allow when suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.*
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    suspend fun bar() {
                        runCatching { 
                            for (x in SuspendingIterator()) {
                                println(x)
                            }
                        }.onFailure { currentCoroutineContext().ensureActive() }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `Report when nested suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.delay
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<String> = iterator { yield("value") }
                    }
                    suspend fun bar() {
                        runCatching { 
                            for (x in SuspendingIterator()) {
                                for (y in SuspendingIterator()) {
                                    println(x + y)
                                }
                            }
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(6, 5)),
                    listOf(SourceLocation(6, 16)),
                )
            }

            @Test
            fun `Allow when nested suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.*
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<String> = iterator { yield("value") }
                    }
                    suspend fun bar() {
                        runCatching { 
                            for (x in SuspendingIterator()) {
                                for (y in SuspendingIterator()) {
                                    println(x + y)
                                }
                            }
                        }.onFailure { currentCoroutineContext().ensureActive() }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `Report when nested iterator with one suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.delay
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    suspend fun bar() {
                        runCatching { 
                            for (x in 1..10) {
                                for (y in SuspendingIterator()) {
                                    println(x.toString() + y)
                                }
                            }
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(6, 5)),
                    listOf(SourceLocation(6, 16)),
                )
            }

            @Test
            fun `Allow when nested iterator with one suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.*
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    suspend fun bar() {
                        runCatching { 
                            for (x in 1..10) {
                                for (y in SuspendingIterator()) {
                                    println(x.toString() + y)
                                }
                            }
                        }.onFailure { currentCoroutineContext().ensureActive() }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `Report when suspending iterator is used withing inlined block`() {
                val code = """
                    import kotlinx.coroutines.*
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    inline fun foo(lambda: () -> Unit) {
                        lambda()
                    }
                    suspend fun bar() {
                        runCatching {
                            foo {
                                for (x in SuspendingIterator()) {
                                    println(x)
                                }
                            }
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(9, 5)),
                    listOf(SourceLocation(9, 16)),
                )
            }

            @Test
            fun `Allow when suspending iterator is used withing inlined block`() {
                val code = """
                    import kotlinx.coroutines.*
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    inline fun foo(lambda: () -> Unit) {
                        lambda()
                    }
                    suspend fun bar() {
                        runCatching {
                            foo {
                                for (x in SuspendingIterator()) {
                                    println(x)
                                }
                            }
                        }.onFailure { currentCoroutineContext().ensureActive() }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `Allow when suspending iterator is used withing non inlined block`() {
                val code = """
                    import kotlinx.coroutines.MainScope
                    import kotlinx.coroutines.delay
                    import kotlinx.coroutines.launch
                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    suspend fun bar() {
                        runCatching { 
                            MainScope().launch { 
                                for (x in SuspendingIterator()) {
                                    println(x)
                                }
                            }
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `Report when suspend function is invoked`() {
                val code = """
                    import kotlinx.coroutines.delay
                    suspend fun foo() {
                        val suspendBlock = suspend {}
                        runCatching {
                            suspendBlock()
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(4, 5)),
                    listOf(SourceLocation(4, 16)),
                )
            }

            @Test
            fun `Allow when suspend function is invoked`() {
                val code = """
                    import kotlinx.coroutines.*
                    suspend fun foo() {
                        val suspendBlock = suspend {}
                        runCatching {
                            suspendBlock()
                        }.onFailure { currentCoroutineContext().ensureActive() }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `Allow when suspend block is passed to non inline function`() {
                val code = """
                    import kotlinx.coroutines.delay
                    import kotlinx.coroutines.MainScope
                    import kotlinx.coroutines.launch
                    fun bar(lambda: suspend () -> Unit) {
                        MainScope().launch { lambda() }
                    }
                    suspend fun foo() {
                        runCatching {
                            bar {
                                delay(1_000)
                            }
                        }
                    }
                """.trimIndent()
                val findings = subject.compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }
    }

    private fun assertFindingsForSuspendCall(
        findings: List<Finding>,
        listOfStartLocation: List<SourceLocation>,
        listOfEndLocation: List<SourceLocation>,
    ) {
        check(listOfEndLocation.size == listOfStartLocation.size)
        assertThat(findings).hasSize(listOfStartLocation.size)
        assertThat(findings).hasStartSourceLocations(*listOfStartLocation.toTypedArray())
        assertThat(findings).hasEndSourceLocations(*listOfEndLocation.toTypedArray())
    }
}
