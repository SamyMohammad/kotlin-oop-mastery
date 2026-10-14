package m12_delegation

import common.log
import common.note
import common.section
import m12_delegation.shop.InMemoryCart
import m12_delegation.shop.LoggingCart

/*
 * L01 - Class delegation with `by`
 *
 *  class LoggingCart(inner: CartApi) : CartApi by inner
 *
 * The compiler writes a forwarding method for every member of CartApi:
 *     override fun remove(name) = inner.remove(name)
 * You override only what you want to change. Composition, without the boilerplate.
 *
 * Gotcha: the delegate does NOT know about the wrapper. If the delegate calls
 * one of its own methods internally, YOUR override is not used.
 */

interface Greeter {
    fun name(): String
    fun greet(): String
}

class BasicGreeter : Greeter {
    override fun name() = "customer"
    override fun greet() = "Hello, ${name()}" // calls ITS OWN name()
}

class VipGreeter(inner: Greeter) : Greeter by inner {
    override fun name() = "VIP customer"
}

fun main() {
    section("1. Decorate one method, forward the rest")
    val cart = LoggingCart(InMemoryCart()) { log("  [log] $it") }
    cart.add("Pen", 2)
    cart.add("Mug", 1)
    cart.remove("Pen")           // forwarded, not logged
    log("lines = ${cart.lines()}")

    section("2. Works with ANY CartApi")
    note("LoggingCart never extends InMemoryCart. Swap the inner cart and logging still works.")

    section("3. The delegation gotcha")
    val vip = VipGreeter(BasicGreeter())
    log("vip.name()  = ${vip.name()}")
    log("vip.greet() = ${vip.greet()}")
    note("greet() was forwarded to BasicGreeter, which called its own name(), not VipGreeter's.")
}
