package m14_extensions_receivers.exercises

import common.check
import common.runCheckSuite

interface M14Ops {
    /** Long cents -> "EGP 12.50" as an extension you write; this delegates to it. */
    fun formatCents(cents: Long): String
    /** String? -> "-" when null or blank, else trimmed. Extension on nullable receiver. */
    fun orDash(s: String?): String
    /** Build with your DSL:
     *  order { line("Pen", 2, 500); line("Mug", 1, 3000); discountPercent(10) }
     *  Render: "Pen x2 = EGP 10.00\nMug x1 = EGP 30.00\nTotal = EGP 36.00"
     *  (total after discount, rounded down). Only `line` and `discountPercent` exist inside the block. */
    fun renderSampleOrder(): String
}

fun runChecks(ops: M14Ops) = runCheckSuite("m14 checks") {
    check("format cents") { ops.formatCents(1250) == "EGP 12.50" && ops.formatCents(7) == "EGP 0.07" }
    check("orDash null") { ops.orDash(null) == "-" }
    check("orDash blank") { ops.orDash("   ") == "-" }
    check("orDash trims") { ops.orDash("  hi ") == "hi" }
    check("dsl render") {
        ops.renderSampleOrder() == "Pen x2 = EGP 10.00\nMug x1 = EGP 30.00\nTotal = EGP 36.00"
    }
}
