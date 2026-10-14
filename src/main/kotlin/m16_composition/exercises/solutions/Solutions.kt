package m16_composition.exercises.solutions

import m16_composition.exercises.M16Ops
import m16_composition.exercises.PriceRule
import m16_composition.exercises.runChecks

object OpsSolution : M16Ops {
    override fun tenPercentOff() = PriceRule { it * 90 / 100 }

    override fun minus(cents: Long) = PriceRule { (it - cents).coerceAtLeast(0) }

    // Each rule HAS the previous result as input - composition, not a subclass per combination
    override fun chain(vararg rules: PriceRule) = PriceRule { start ->
        rules.fold(start) { acc, rule -> rule.apply(acc) }
    }

    override fun layout(header: (() -> String)?, body: () -> String, footer: (() -> String)?): String =
        listOfNotNull(
            header?.let { "[${it()}]" },
            body(),
            footer?.let { "[${it()}]" },
        ).joinToString("\n")
}

fun main() = runChecks(OpsSolution)
