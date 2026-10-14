package m16_composition.exercises

/*
 * Module 16 exercises. No class hierarchy allowed - build behaviour by combining small pieces.
 */

object MyOps : M16Ops {
    /** PriceRule { cents -> ... } rounding the RESULT down: 999 -> 899 */
    override fun tenPercentOff(): PriceRule = TODO()

    /** Never below 0 (coerceAtLeast). */
    override fun minus(cents: Long): PriceRule = TODO()

    /** Apply each rule to the result of the previous one. rules.fold(...) is one neat way. */
    override fun chain(vararg rules: PriceRule): PriceRule = TODO()

    /** Build a list of lines: header?.let { "[${it()}]" }, body(), footer?.let { ... } - then join. */
    override fun layout(header: (() -> String)?, body: () -> String, footer: (() -> String)?): String = TODO()
}

fun main() = runChecks(MyOps)
