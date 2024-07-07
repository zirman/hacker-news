package com.monoid.hackernews.detekt.extensions.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class CatchingCoroutineCancellation(config: Config = Config.empty) : Rule(config) {
    private val currentCoroutineContextNames: MutableList<Name?> = mutableListOf()
    private val ensureActiveNames: MutableList<Name?> = mutableListOf()

    private val jobCancellationExceptionNames: MutableList<Name> = mutableListOf(
        // default imported names that could be a JobCancellationException
        Name.identifier("Throwable"),
        Name.identifier("Exception"),
    )

    override val issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Defect,
        description = "This rule reports not handling coroutine cancellation exceptions correctly.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)
        val importPath = importDirective.importPath
        when (importPath?.fqName?.asString()) {
            "kotlinx.coroutines.currentCoroutineContext" -> {
                currentCoroutineContextNames += importPath.importedName
            }

            "kotlinx.coroutines.ensureActive" -> {
                ensureActiveNames += importPath.importedName
            }

            // imported names
            "kotlinx.coroutines.CancellationException",

            // aliases
            "kotlin.Throwable",
            "kotlin.Exception",
            "java.lang.Exception" -> {
                importPath.importedName?.let {
                    jobCancellationExceptionNames += it
                }
            }
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        function.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: return
        function.accept(catchSectionVisitor)
    }

    private val catchSectionVisitor = object : KtTreeVisitorVoid() {
        private var enteredNamedFunction = false
        override fun visitElement(element: PsiElement) {
            // prevent entering nested named functions
            if (element !is KtNamedFunction) {
                super.visitElement(element)
            } else if (enteredNamedFunction.not()) {
                enteredNamedFunction = true
                super.visitElement(element)
                enteredNamedFunction = false
            }
        }

        override fun visitCatchSection(catchClause: KtCatchClause) {
            super.visitCatchSection(catchClause)

            catchClause.catchParameter?.typeReference?.getTypeText()
                ?.takeIf { catchParameterType ->
                    jobCancellationExceptionNames.any { it.identifier == catchParameterType }
                }
                ?.let { catchClause.catchBody?.children?.firstOrNull() as? KtDotQualifiedExpression }
                ?.let { element ->
                    ((element.receiverExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)
                        ?.getReferencedNameAsName()
                        ?.takeIf { currentCoroutineContextNames.contains(it) }
                        ?.let {
                            (
                                (element.selectorExpression as? KtCallExpression)?.calleeExpression as?
                                    KtNameReferenceExpression
                                )
                                ?.getReferencedNameAsName()
                        }
                        ?.takeIf { ensureActiveNames.contains(it) }
                }
                ?: report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(catchClause),
                        message = "Check for coroutine cancellation with `currentCoroutineContext().ensureActive()`",
                    ),
                )
        }
    }
}
