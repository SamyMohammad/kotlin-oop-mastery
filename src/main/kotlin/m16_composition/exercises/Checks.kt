package m16_composition.exercises

import common.check
import common.runCheckSuite

fun interface PriceRule { fun apply(cents: Long): Long }

interface M16Ops {
    /** 10% off, rounded down */
    fun tenPercentOff(): PriceRule
    /** subtract fixed amount, floor at 0 */
    fun minus(cents: Long): PriceRule
    /** Compose rules left to right WITHOUT inheritance. */
    fun chain(vararg rules: PriceRule): PriceRule
    /** Slot API: header and footer are optional. Output lines joined with "\n":
     *  "[header]" line only if header != null, then body(), then "[footer]" line only if footer != null.
     *  header/footer lambdas return the text inside the brackets. */
    fun layout(header: (() -> String)?, body: () -> String, footer: (() -> String)?): String
}

fun runChecks(ops: M16Ops) = runCheckSuite("m16 checks") {
    check("ten percent") { ops.tenPercentOff().apply(999) == 899L }
    check("minus floors") { ops.minus(500).apply(300) == 0L }
    check("chain order") { ops.chain(ops.tenPercentOff(), ops.minus(100)).apply(1000) == 800L }
    check("chain other order") { ops.chain(ops.minus(100), ops.tenPercentOff()).apply(1000) == 810L }
    check("chain empty is identity") { ops.chain().apply(123) == 123L }
    check("layout full") { ops.layout({ "Cart" }, { "2 items" }, { "Pay" }) == "[Cart]\n2 items\n[Pay]" }
    check("layout no slots") { ops.layout(null, { "only" }, null) == "only" }
}
