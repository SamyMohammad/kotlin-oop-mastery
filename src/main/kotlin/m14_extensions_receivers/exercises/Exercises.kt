package m14_extensions_receivers.exercises

/*
 * Module 14 exercises.
 *
 * 1) Write `fun Long.toEgp(): String` and use it in formatCents.
 * 2) Write `fun String?.orDash(): String` and use it in orDash.
 * 3) Build a tiny DSL:
 *      class OrderBuilder { fun line(name: String, qty: Int, unitCents: Long); fun discountPercent(p: Int); fun render(): String }
 *      fun order(block: OrderBuilder.() -> Unit): String = OrderBuilder().apply(block).render()
 */

object MyOps : M14Ops {
    override fun formatCents(cents: Long): String = TODO()
    override fun orDash(s: String?): String = TODO()

    /** return order { line("Pen", 2, 500); line("Mug", 1, 3000); discountPercent(10) } */
    override fun renderSampleOrder(): String = TODO()
}

fun main() = runChecks(MyOps)
