package m15_operators_equality

import common.log
import common.note
import common.section

/*
 * L02 - Index, contains, invoke, rangeTo, componentN
 *
 *   cart["pen"]         -> cart.get("pen")
 *   cart["pen"] = 3     -> cart.set("pen", 3)
 *   "pen" in cart       -> cart.contains("pen")
 *   rule(10_000)        -> rule.invoke(10_000)
 *   a..b                -> a.rangeTo(b)
 *   val (a, b) = x      -> x.component1(), x.component2()
 */

class Cart {
    private val qty = linkedMapOf<String, Int>()

    operator fun get(name: String): Int = qty[name] ?: 0
    operator fun set(name: String, value: Int) {
        if (value <= 0) qty.remove(name) else qty[name] = value
    }
    operator fun contains(name: String) = name in qty

    override fun toString() = qty.toString()
}

// A callable object
class DiscountRule(private val percent: Int) {
    operator fun invoke(cents: Long): Long = cents * (100 - percent) / 100
}

// Destructuring a NON-data class
class Dimensions(val width: Int, val height: Int) {
    operator fun component1() = width
    operator fun component2() = height
}

data class Day(val n: Int) : Comparable<Day> {
    override fun compareTo(other: Day) = n.compareTo(other.n)
    operator fun rangeTo(other: Day) = (n..other.n).map(::Day)
}

fun main() {
    section("1. get / set / contains")
    val cart = Cart()
    cart["pen"] = 2
    cart["mug"] = 1
    cart["pen"] = cart["pen"] + 1
    cart["mug"] = 0
    log("cart = $cart")
    log("'pen' in cart = ${"pen" in cart}, 'mug' in cart = ${"mug" in cart}")

    section("2. invoke")
    val tenOff = DiscountRule(10)
    log("tenOff(10000) = ${tenOff(10_000)}")
    note("An object you can call like a function.")

    section("3. componentN on a regular class")
    val (w, h) = Dimensions(1080, 1920)
    log("w=$w h=$h")

    section("4. rangeTo")
    log((Day(1)..Day(3)).map { "day ${it.n}" })
}
