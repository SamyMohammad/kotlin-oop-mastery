package m17_solid

import common.log
import common.note
import common.section

/*
 * L02 - O: Open/Closed Principle
 *
 * "Open for extension, closed for modification."
 * Add new behaviour by ADDING code (a new class/value), not by EDITING tested code.
 *
 * BEFORE: every new coupon type edits the `when` inside the calculator.
 * AFTER:  the calculator takes a DiscountRule; new coupons are new rules.
 */

// ---------- BEFORE ----------
fun discountBefore(type: String, subtotal: Long): Long = when (type) {
    "PERCENT10" -> subtotal / 10
    "FIXED50" -> 5_000
    // "BOGO" -> ...   <- adding a coupon means editing (and re-testing) this function
    else -> 0
}

// ---------- AFTER ----------
fun interface DiscountRule {
    fun amountOff(subtotal: Long): Long
}

class CartTotals(private val rules: List<DiscountRule>) {
    fun total(subtotal: Long): Long {
        val off = rules.sumOf { it.amountOff(subtotal) }
        return (subtotal - off).coerceAtLeast(0)
    }
}

val percent10 = DiscountRule { it / 10 }
val fixed50 = DiscountRule { 5_000 }

fun main() {
    section("BEFORE")
    log("PERCENT10 on 20000 -> ${20_000 - discountBefore("PERCENT10", 20_000)}")
    log("FIXED50  on 20000 -> ${20_000 - discountBefore("FIXED50", 20_000)}")

    section("AFTER: CartTotals never changes")
    log("percent10           -> ${CartTotals(listOf(percent10)).total(20_000)}")
    log("percent10 + fixed50 -> ${CartTotals(listOf(percent10, fixed50)).total(20_000)}")

    section("A brand new rule, added from outside")
    val loyalty = DiscountRule { subtotal -> if (subtotal > 15_000) 1_000 else 0 }
    log("percent10 + loyalty -> ${CartTotals(listOf(percent10, loyalty)).total(20_000)}")
    note("We extended the system without opening CartTotals. Its tests stay valid.")
}
