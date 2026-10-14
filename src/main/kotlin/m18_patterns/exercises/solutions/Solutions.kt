package m18_patterns.exercises.solutions

import m18_patterns.exercises.LegacyFx
import m18_patterns.exercises.M18Factory
import m18_patterns.exercises.OrderMachine
import m18_patterns.exercises.OrderState
import m18_patterns.exercises.PriceSource
import m18_patterns.exercises.runChecks
import kotlin.math.floor

// ---------- State: each state knows its own allowed transitions ----------
sealed class State(override val name: String) : OrderState {
    open fun pay(): State? = null
    open fun ship(): State? = null
    open fun deliver(): State? = null
    open fun cancel(): State? = null

    data object Created : State("Created") {
        override fun pay() = Paid
        override fun cancel() = Cancelled
    }
    data object Paid : State("Paid") {
        override fun ship() = Shipped
        override fun cancel() = Cancelled
    }
    data object Shipped : State("Shipped") {
        override fun deliver() = Delivered
    }
    data object Delivered : State("Delivered")
    data object Cancelled : State("Cancelled")
}

class Machine : OrderMachine {
    override var state: State = State.Created
        private set

    private fun move(next: State?): Boolean {
        if (next == null) return false
        state = next
        return true
    }

    override fun pay() = move(state.pay())
    override fun ship() = move(state.ship())
    override fun deliver() = move(state.deliver())
    override fun cancel() = move(state.cancel())
}

// ---------- Decorators ----------
class Vat(private val inner: PriceSource) : PriceSource {
    override fun priceCents() = inner.priceCents() * 114 / 100
}

class Coupon(private val inner: PriceSource, private val offCents: Long) : PriceSource {
    override fun priceCents() = (inner.priceCents() - offCents).coerceAtLeast(0)
}

// ---------- Adapter ----------
class UsdPrice(private val fx: LegacyFx, private val usd: Double) : PriceSource {
    override fun priceCents() = floor(fx.usdToEgp(usd) * 100).toLong()
}

// ---------- Observer ----------
class Observable<T> {
    private val listeners = mutableListOf<(T) -> Unit>()

    fun subscribe(listener: (T) -> Unit): () -> Unit {
        listeners += listener
        return { listeners -= listener }
    }

    fun publish(value: T) = listeners.toList().forEach { it(value) }
}

object FactorySolution : M18Factory {
    override fun machine(): OrderMachine = Machine()
    override fun withVat(source: PriceSource): PriceSource = Vat(source)
    override fun withCoupon(source: PriceSource, offCents: Long): PriceSource = Coupon(source, offCents)
    override fun fromUsd(fx: LegacyFx, usd: Double): PriceSource = UsdPrice(fx, usd)
    override fun <T> observable(): Pair<((T) -> Unit) -> (() -> Unit), (T) -> Unit> {
        val o = Observable<T>()
        return Pair({ listener -> o.subscribe(listener) }, { value -> o.publish(value) })
    }
}

fun main() = runChecks(FactorySolution)
