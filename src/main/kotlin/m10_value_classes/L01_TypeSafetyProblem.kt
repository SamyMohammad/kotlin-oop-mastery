package m10_value_classes

import common.log
import common.note
import common.section

/*
 * L01 - Primitive obsession
 *
 * When everything is an Int or a Long, the compiler cannot tell a product id
 * from a quantity from a price. Swapped arguments compile and run - wrongly.
 */

class RawCart {
    private val lines = linkedMapOf<Int, Int>() // productId -> qty

    fun add(productId: Int, qty: Int) {
        lines[productId] = (lines[productId] ?: 0) + qty
    }

    override fun toString() = lines.entries.joinToString { "product ${it.key} x${it.value}" }
}

fun main() {
    section("1. The intended call")
    val cart = RawCart()
    val productId = 1042
    val qty = 2
    cart.add(productId, qty)
    log(cart)

    section("2. The swapped call")
    val oops = RawCart()
    oops.add(qty, productId) // compiles: both are Int
    log(oops)
    note("We just ordered 1042 units of product #2. No error, no warning.")

    section("3. Same for money")
    val priceCents = 4500L
    val discountCents = 500L
    log("correct: ${priceCents - discountCents}")
    log("swapped: ${discountCents - priceCents}")
    note("L02 fixes this with value classes - at zero runtime cost.")
}
