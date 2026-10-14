package m10_value_classes

import common.log
import common.note
import common.section
import m10_value_classes.shop.Price

/*
 * L03 - When is a value class an object?
 *
 * UNBOXED (just a long on the JVM, zero allocation):
 *   val p: Price = Price(500)
 *   fun pay(p: Price)
 *
 * BOXED (a real Price object is created):
 *   val p: Price? = Price(500)        nullable
 *   val list: List<Price>              generics
 *   val any: Any = Price(500)          used as Any / an interface
 *
 * On the JVM, functions taking value classes get "mangled" names like pay-AbCd123
 * so that pay(Price) and pay(Long) do not clash. Java callers cannot see them easily.
 *
 * `===` on value classes is forbidden: they have no identity.
 */

fun pay(p: Price) = "paying ${p.format()}"

fun main() {
    section("1. Equality is by value")
    val a = Price(500)
    val b = Price(500)
    log("a == b ? ${if (a == b) "same" else "different"}")
    // a === b   <- error: Identity equality for arguments of types 'Price' and 'Price' is prohibited.

    section("2. Unboxed and boxed uses")
    log(pay(a))                          // unboxed: passes a plain long
    val maybe: Price? = Price(700)       // boxed: nullable
    val prices: List<Price> = listOf(a, b, Price(900)) // boxed: generic
    log("nullable: ${maybe?.format()}")
    log("sum of list: ${prices.fold(Price(0)) { acc, p -> acc + p }.format()}")

    section("3. What it looks like at runtime")
    val boxed: Any = a
    log("runtime class when boxed: ${boxed::class.simpleName}")
    log("toString: $a")
    note("Value classes give type safety for free in most code; boxing happens only at the edges.")
}
