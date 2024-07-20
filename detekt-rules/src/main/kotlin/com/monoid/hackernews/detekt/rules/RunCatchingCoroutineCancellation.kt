package com.monoid.hackernews.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.builtins.StandardNames.COROUTINES_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getPossiblyQualifiedCallExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getArgumentByParameterIndex
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

// Handling coroutine cancellation to avoid bugs
// https://betterprogramming.pub/the-silent-killer-thats-crashing-your-coroutines-9171d1e8f79b
// https://github.com/Kotlin/kotlinx.coroutines/issues/3658

@RequiresTypeResolution
class RunCatchingCoroutineCancellation(config: Config) : Rule(config) {

    // Based on SuspendFunSwallowedCancellation code from Detekt project:
    // https://github.com/detekt/detekt/blob/main/detekt-rules-coroutines/src/main/kotlin/io/gitlab/arturbosch/detekt/rules/coroutines/SuspendFunSwallowedCancellation.kt

    override val issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Defect,
        description = "`runCatching`, `recoverCatching` and `mapCatching` does not handle coroutine cancellation. " +
            "Check for coroutine cancellation using `.onFailure { currentCoroutineContext().ensureActive() }`.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.shouldReport()) {
            expression.report()
        }
    }

    private fun KtCallExpression.shouldReport(): Boolean =
        isCatchingCall() && hasSuspendCalls() && isEnsureActiveCalled().not()

    private fun KtCallExpression.report() {
        report(
            CodeSmell(
                issue,
                Entity.from(calleeExpression?.safeAs<PsiElement>() ?: this),
                "`runCatching`, `recoverCatching` or `mapCatching` has a suspend call inside. Check for coroutine " +
                    "cancellation by chaining with `.onFailure { currentCoroutineContext().ensureActive() }` to " +
                    "prevent [coroutine cancellation bugs]" +
                    "(https://betterprogramming.pub/the-silent-killer-thats-crashing-your-coroutines-9171d1e8f79b).",
            )
        )
    }

    private fun KtCallExpression.isEnsureActiveCalled(): Boolean {
        val dotQualifiedExpression = when (getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameSafe) {
            RUN_CATCHING_FQ_NAME -> getStrictParentOfType<KtDotQualifiedExpression>()
            else -> getStrictParentOfType<KtDotQualifiedExpression>()?.getStrictParentOfType<KtDotQualifiedExpression>()
        }

        return dotQualifiedExpression?.isOnFailureCall() == true &&
            dotQualifiedExpression.getLambdaExpressionArgument()?.isEnsureActiveCalled() == true
    }

    private fun KtDotQualifiedExpression.isOnFailureCall(): Boolean =
        getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameSafe == ON_FAILURE_FQ_NAME

    private fun KtDotQualifiedExpression.getLambdaExpressionArgument(): KtLambdaExpression? =
        getPossiblyQualifiedCallExpression()
            ?.getArgumentByParameterIndex(0, bindingContext)
            ?.get(0)
            ?.getArgumentExpression()
            ?.safeAs<KtLambdaExpression>()

    private fun KtLambdaExpression.isEnsureActiveCalled(): Boolean = bodyExpression
        ?.statements
        ?.firstOrNull()
        ?.isEnsureActiveExpression() == true

    private fun KtCallExpression.isCatchingCall(): Boolean =
        getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameSafe
            ?.let { CATCHING_FQ_NAMES.contains(it) } == true

    private fun KtCallExpression.hasSuspendCalls(): Boolean =
        anyDescendantOfType<KtExpression>(
            canGoInside = { this == it || shouldTraverseInside(it) },
        ) { it.hasSuspendCalls() }

    @Suppress("ReturnCount")
    private fun KtExpression.hasSuspendCalls(): Boolean = when (this) {
        is KtForExpression -> listOf(
            bindingContext[BindingContext.LOOP_RANGE_ITERATOR_RESOLVED_CALL, loopRange],
            bindingContext[BindingContext.LOOP_RANGE_HAS_NEXT_RESOLVED_CALL, loopRange],
            bindingContext[BindingContext.LOOP_RANGE_NEXT_RESOLVED_CALL, loopRange],
        ).any { it?.resultingDescriptor?.isSuspend == true }

        is KtCallExpression, is KtOperationExpression -> getResolvedCall(bindingContext)
            ?.resultingDescriptor?.safeAs<FunctionDescriptor>()
            ?.isSuspend == true

        is KtNameReferenceExpression -> getResolvedCall(bindingContext)
            ?.resultingDescriptor?.safeAs<PropertyDescriptor>()
            ?.fqNameSafe == COROUTINE_CONTEXT_FQ_NAME

        else -> false
    }

    private fun KtExpression.isEnsureActiveExpression(): Boolean = when {
        this !is KtCallableReferenceExpression -> getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull() == ENSURE_ACTIVE_FQ_NAME

        parent is KtValueArgument -> callableReference.isEnsureActiveExpression()
        else -> false
    }

    private fun shouldTraverseInside(
        psiElement: PsiElement,
    ): Boolean = when (psiElement) {
        is KtCallExpression -> psiElement.getResolvedCall(bindingContext)
            ?.resultingDescriptor?.safeAs<FunctionDescriptor>()
            ?.let { CATCHING_FQ_NAMES.contains(it.fqNameSafe).not() && it.isInline } == true

        is KtValueArgument -> psiElement.getStrictParentOfType<KtCallExpression>()
            ?.getResolvedCall(bindingContext)
            ?.getParameterForArgument(psiElement)
            ?.let { it.isCrossinline.not() && it.isNoinline.not() } == true

        else -> true
    }

    companion object {
        private val RUN_CATCHING_FQ_NAME = FqName("kotlin.runCatching")
        private val CATCHING_FQ_NAMES = listOf(
            RUN_CATCHING_FQ_NAME,
            FqName("kotlin.recoverCatching"),
            FqName("kotlin.mapCatching"),
        )
        private val ON_FAILURE_FQ_NAME = FqName("kotlin.onFailure")
        private val ENSURE_ACTIVE_FQ_NAME = FqName("kotlinx.coroutines.ensureActive")

        // Based on code from Kotlin project:
        // https://github.com/JetBrains/kotlin/commit/87bbac9d43e15557a2ff0dc3254fd41a9d5639e1
        private val COROUTINE_CONTEXT_FQ_NAME = COROUTINES_PACKAGE_FQ_NAME.child(Name.identifier("coroutineContext"))
    }
}
