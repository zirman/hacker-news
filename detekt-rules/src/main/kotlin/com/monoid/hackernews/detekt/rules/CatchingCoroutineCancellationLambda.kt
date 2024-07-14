package com.monoid.hackernews.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypes
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypesAndPredicate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

@RequiresTypeResolution
class CatchingCoroutineCancellationLambda(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Defect,
        description = "This rule reports not handling coroutine cancellation exceptions correctly.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitCatchSection(catchClause: KtCatchClause) {
        super.visitCatchSection(catchClause)
        checkAndReport(catchClause)
    }

    private fun checkAndReport(catchClause: KtCatchClause) {
        if (shouldReport(catchClause)) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(catchClause),
                    message = "Check for coroutine cancellation with `currentCoroutineContext().ensureActive()`",
                )
            )
        }
    }

    private fun PsiElement.isSuspendAllowed(): Boolean = when (this) {
        is KtValueArgument -> isSuspendAllowed()
        is KtNamedFunction -> isSuspendAllowed()
        is KtLambdaExpression -> isSuspendAllowed()
        else -> false
    }

    private fun KtValueArgument.isSuspendAllowed(): Boolean {
        val parent = getParentOfTypes(strict = true, KtCallExpression::class.java) ?: return false
        val valueParameterDescriptor =
            parent.getResolvedCall(bindingContext)?.getParameterForArgument(this)
        return valueParameterDescriptor?.returnType?.isSuspendFunctionType == true
    }

    private fun KtLambdaExpression.isSuspendAllowed(): Boolean {
        val parent = getParentOfTypes(strict = true, KtProperty::class.java) ?: return false
        return bindingContext[BindingContext.VARIABLE, parent]?.returnType?.isSuspendFunctionType == true
    }

    private fun KtNamedFunction.isSuspendAllowed(): Boolean =
        bindingContext[BindingContext.FUNCTION, this]?.isSuspend == true

    private fun shouldReport(catchClause: KtCatchClause): Boolean =
        catchClause.catchesCoroutineCancellation() &&
            catchClause.nearestParent()?.isSuspendAllowed() == true &&
            catchClause.isEnsureActiveCalled().not()

    private fun KtCatchClause.catchesCoroutineCancellation(): Boolean =
        catchParameter?.typeReference?.let { bindingContext[BindingContext.TYPE, it] }
            ?.constructor?.declarationDescriptor?.fqNameOrNull()
            ?.asString()
            ?.let { possibleCoroutineCancellationFqNames.contains(it) } == true

    private fun KtCatchClause.nearestParent(): PsiElement? = getParentOfTypesAndPredicate(
        strict = false,
        KtNamedFunction::class.java,
        KtValueArgument::class.java,
        KtLambdaExpression::class.java,
    ) {
        when (it) {
            is KtValueArgument -> it.isNearestParentForSuspension()
            is KtNamedFunction -> true
            is KtLambdaExpression ->
                it.getParentOfType<KtProperty>(strict = true, KtValueArgument::class.java) !=
                    null

            else -> false
        }
    }

    private fun KtCatchClause.isEnsureActiveCalled(): Boolean =
        catchBody?.children?.firstOrNull()
            .let { it as? KtExpression }
            ?.isEnsureActiveExpression() == true

    @Suppress("ReturnCount")
    private fun KtValueArgument.isNearestParentForSuspension(): Boolean {
        val parent = getParentOfTypes(true, KtCallExpression::class.java) ?: return false
        val valueParameterDescriptor = parent.getResolvedCall(bindingContext)
            ?.getParameterForArgument(this)
            ?: return false
        val functionDescriptor = parent.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.let { it as? FunctionDescriptor }
            ?: return false
        return functionDescriptor.isInline.not() ||
            (valueParameterDescriptor.isNoinline || valueParameterDescriptor.isCrossinline)
    }

    private fun KtExpression.isEnsureActiveExpression(): Boolean = when {
        this !is KtCallableReferenceExpression -> getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?.asString() == "kotlinx.coroutines.ensureActive"

        parent is KtValueArgument -> callableReference.isEnsureActiveExpression()
        else -> false
    }

    companion object {
        private val possibleCoroutineCancellationFqNames = listOf(
            "kotlin.Throwable",
            "java.lang.Exception",
            "java.lang.Throwable",
            "java.util.concurrent.CancellationException",
        )
    }
}
