package m14_extensions_receivers.exercises.solutions

import m14_extensions_receivers.exercises.M14Ops
import m14_extensions_receivers.exercises.runChecks

fun Long.toEgp(): String = "EGP %d.%02d".format(this / 100, this % 100)

// Nullable receiver: callable on null without ?.
fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this.trim()

@DslMarker
annotation class OrderDsl

@OrderDsl
class OrderBuilder {
    private val lines = mutableListOf<Triple<String, Int, Long>>()
    private var discount = 0

    fun line(name: String, qty: Int, unitCents: Long) {
        lines += Triple(name, qty, unitCents)
    }

    fun discountPercent(percent: Int) {
        discount = percent
    }

    fun render(): String {
        val rows = lines.map { (name, qty, unit) -> "$name x$qty = ${(qty * unit).toEgp()}" }
        val subtotal = lines.sumOf { (_, qty, unit) -> qty * unit }
        val total = subtotal * (100 - discount) / 100
        return (rows + "Total = ${total.toEgp()}").joinToString("\n")
    }
}

// Lambda with receiver: inside the block, `this` is an OrderBuilder
fun order(block: OrderBuilder.() -> Unit): String = OrderBuilder().apply(block).render()

object OpsSolution : M14Ops {
    override fun formatCents(cents: Long) = cents.toEgp()
    override fun orDash(s: String?) = s.orDash()
    override fun renderSampleOrder() = order {
        line("Pen", 2, 500)
        line("Mug", 1, 3000)
        discountPercent(10)
    }
}

fun main() = runChecks(OpsSolution)
