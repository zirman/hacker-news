package com.monoid.hackernews.detekt.extensions

import com.monoid.hackernews.detekt.extensions.rules.CatchingCoroutineCancellation
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

internal class HackerRulesProvider : RuleSetProvider {
    override val ruleSetId: String = "hackerRules"
    override fun instance(config: Config): RuleSet = RuleSet(
        id = ruleSetId,
        rules = listOf(
            CatchingCoroutineCancellation(config),
        ),
    )
}
