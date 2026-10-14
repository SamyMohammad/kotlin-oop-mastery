package m03_inheritance

import common.log
import common.note
import common.section
import m03_inheritance.shop.CardPayment
import m03_inheritance.shop.Payment

/*
 * L04 - What every class inherits from Any
 *
 *  open fun equals(other: Any?): Boolean   default: same object?  (identity)
 *  open fun hashCode(): Int                default: identity based
 *  open fun toString(): String             default: ClassName@hexHash
 */

class Coupon(val code: String)                      // inherits Any's defaults

class PrettyCoupon(val code: String) {
    override fun toString() = "Coupon($code)"
}

class ValueCoupon(val code: String) {
    override fun equals(other: Any?): Boolean = other is ValueCoupon && other.code == code
    override fun hashCode(): Int = code.hashCode()
    override fun toString() = "ValueCoupon($code)"
}

fun same(a: Any, b: Any) = if (a == b) "same" else "different"

@Suppress("USELESS_IS_CHECK") // `is Any` is always true - that is the point
fun main() {
    section("1. Default equals = identity")
    log("Coupon(SAVE10) vs Coupon(SAVE10): ${same(Coupon("SAVE10"), Coupon("SAVE10"))}")
    log("default toString looks like Coupon@<hash>: ${Coupon("X").toString().startsWith("m03_inheritance.Coupon@")}")

    section("2. Overriding toString")
    log(PrettyCoupon("SAVE10"))

    section("3. Overriding equals and hashCode together")
    log("ValueCoupon vs ValueCoupon: ${same(ValueCoupon("SAVE10"), ValueCoupon("SAVE10"))}")
    log("set size = ${setOf(ValueCoupon("A"), ValueCoupon("A"), ValueCoupon("B")).size}")
    note("data classes (m07) generate all three for you.")

    section("4. is-a up the hierarchy")
    val pay: Any = CardPayment(100, "1111")
    log("is CardPayment? ${pay is CardPayment}")
    log("is Payment?     ${pay is Payment}")
    log("is Any?         ${pay is Any}")
}
