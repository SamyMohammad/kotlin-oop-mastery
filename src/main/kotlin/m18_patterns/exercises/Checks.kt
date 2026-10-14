package m18_patterns.exercises

import common.check
import common.runCheckSuite

/** Not sealed on purpose: sealed subtypes must live in this package, but the solutions live in a sub-package. */
interface OrderState { val name: String }

interface OrderMachine {
    val state: OrderState
    /** Allowed: Created -pay-> Paid -ship-> Shipped -deliver-> Delivered; Created|Paid -cancel-> Cancelled.
     *  Returns true when the transition happened, false (state unchanged) otherwise. */
    fun pay(): Boolean
    fun ship(): Boolean
    fun deliver(): Boolean
    fun cancel(): Boolean
}

interface PriceSource { fun priceCents(): Long }

/** Legacy API to adapt - do not change. */
class LegacyFx { fun usdToEgp(usd: Double): Double = usd * 50.0 }

interface M18Factory {
    /** State pattern: new machine in "Created". state.name is one of Created, Paid, Shipped, Delivered, Cancelled. */
    fun machine(): OrderMachine
    /** Decorator: wraps source, adds 14% VAT rounded down. */
    fun withVat(source: PriceSource): PriceSource
    /** Decorator: wraps source, subtracts fixed cents, floor 0. */
    fun withCoupon(source: PriceSource, offCents: Long): PriceSource
    /** Adapter: a PriceSource whose priceCents() is LegacyFx.usdToEgp(usd) * 100, rounded down. */
    fun fromUsd(fx: LegacyFx, usd: Double): PriceSource
    /** Observer: subscribe returns an unsubscribe function; publish delivers to current subscribers in subscription order. */
    fun <T> observable(): Pair<((T) -> Unit) -> (() -> Unit), (T) -> Unit>
}

fun runChecks(f: M18Factory) = runCheckSuite("m18 checks") {
    check("happy path") { val m = f.machine(); m.pay() && m.ship() && m.deliver() && m.state.name == "Delivered" }
    check("cannot ship unpaid") { val m = f.machine(); !m.ship() && m.state.name == "Created" }
    check("cancel after ship refused") { val m = f.machine(); m.pay(); m.ship(); !m.cancel() && m.state.name == "Shipped" }
    check("cancel from paid") { val m = f.machine(); m.pay(); m.cancel() && m.state.name == "Cancelled" }
    val base = object : PriceSource { override fun priceCents() = 10_000L }
    check("vat decorator") { f.withVat(base).priceCents() == 11_400L }
    check("stacked decorators order") { f.withVat(f.withCoupon(base, 1_000)).priceCents() == 10_260L }
    check("adapter") { f.fromUsd(LegacyFx(), 2.5).priceCents() == 12_500L }
    check("observer") {
        val (subscribe, publish) = f.observable<String>()
        val got = mutableListOf<String>()
        val off = subscribe { got += "a:$it" }; subscribe { got += "b:$it" }
        publish("1"); off(); publish("2")
        got == listOf("a:1", "b:1", "b:2")
    }
}
