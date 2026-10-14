package m05_interfaces

import common.log
import common.note
import common.section

/*
 * L04 - fun interface (SAM: Single Abstract Method)
 *
 * A `fun interface` has exactly one abstract method, so a lambda can stand in for it:
 *     val rule = PriceRule { cents -> cents - 100 }
 *
 * Compare with a plain function type  (Long) -> Long:
 *   - fun interface: has a NAME, can have default methods, shows intent in signatures
 *   - function type: lighter, no declaration needed
 */

fun interface PriceRule {
    fun apply(cents: Long): Long

    // fun interfaces may have non-abstract members
    fun then(next: PriceRule): PriceRule = PriceRule { next.apply(apply(it)) }
}

// The same idea as a bare function type
typealias PriceFn = (Long) -> Long

fun checkout(subtotal: Long, rule: PriceRule) = rule.apply(subtotal)
fun checkoutFn(subtotal: Long, fn: PriceFn) = fn(subtotal)

// Old style: a full class implementing the interface
class TenPercentOff : PriceRule {
    override fun apply(cents: Long) = cents - cents / 10
}

fun main() {
    section("1. Class vs SAM lambda")
    log("class:  ${checkout(10_000, TenPercentOff())}")
    log("lambda: ${checkout(10_000, PriceRule { it - 500 })}")
    log("trailing lambda: ${checkout(10_000) { it / 2 }}")

    section("2. Default method on a fun interface")
    val combined = TenPercentOff().then(PriceRule { it - 500 })
    log("10% then -500: ${combined.apply(10_000)}")

    section("3. Plain function type")
    log("fn: ${checkoutFn(10_000) { it - 1 }}")
    note("Use fun interface when the concept deserves a name or extra methods.")
}
