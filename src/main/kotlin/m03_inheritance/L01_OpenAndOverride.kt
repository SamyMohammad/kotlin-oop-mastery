package m03_inheritance

import common.log
import common.note
import common.section
import m03_inheritance.shop.CardPayment
import m03_inheritance.shop.CashOnDelivery
import m03_inheritance.shop.Payment

/*
 * L01 - open, override, super, final override
 *
 * Kotlin classes and members are FINAL by default.
 *   class Product
 *   class Book : Product()   <- error: This type is final, so it cannot be inherited from.
 *
 * To allow inheritance:  open class / open fun
 * To replace behaviour:  override fun
 * An override is itself open, unless you write `final override`.
 */

open class Discount {
    open fun percent(): Int = 0
    fun label() = "${percent()}% off" // not open: subclasses cannot change it
}

open class SeasonalDiscount : Discount() {
    override fun percent(): Int = 10
}

class BlackFridayDiscount : SeasonalDiscount() {
    // calls the parent's version, then builds on it
    final override fun percent(): Int = super.percent() * 5
}

fun main() {
    section("1. Overriding shop payments")
    val payments = listOf(Payment(10_000), CardPayment(10_000, "4242"), CashOnDelivery(10_000))
    for (p in payments) log("${p.describe()} | fee=${p.fee()} total=${p.total()}")
    note("total() is not open - every payment computes it the same way, using its own fee().")

    section("2. super builds on the parent")
    log("Discount:            ${Discount().label()}")
    log("SeasonalDiscount:    ${SeasonalDiscount().label()}")
    log("BlackFridayDiscount: ${BlackFridayDiscount().label()}")
    note("BlackFriday = super.percent() * 5 = 10 * 5.")

    section("3. final override")
    log("A class extending BlackFridayDiscount could not override percent() again.")
    log("BlackFridayDiscount itself is final (no `open`), so nobody can extend it anyway.")
}
