package m18_patterns

import common.log
import common.note
import common.section

/*
 * L03 - Observer and State
 *
 * OBSERVER: an object (subject) keeps a list of listeners and notifies them
 *           when something changes. Listeners can unsubscribe.
 *           (StateFlow and Compose State are industrial-strength observers.)
 *
 * STATE:    an object changes behaviour when its internal state changes.
 *           Each state is its own type and decides which transitions it allows,
 *           so there is no giant `when` over a status string.
 */

class StockLevel(initial: Int) {
    private val listeners = mutableListOf<(Int) -> Unit>()

    var value: Int = initial
        private set

    fun subscribe(listener: (Int) -> Unit): () -> Unit {
        listeners += listener
        listener(value) // new subscribers get the current value, like StateFlow
        return { listeners -= listener }
    }

    fun update(newValue: Int) {
        if (newValue == value) return // no change, no notification (StateFlow does the same)
        value = newValue
        listeners.toList().forEach { it(newValue) }
    }
}

sealed class OrderState(val name: String) {
    open fun pay(): OrderState = reject("pay")
    open fun ship(): OrderState = reject("ship")
    open fun cancel(): OrderState = reject("cancel")

    protected fun reject(action: String): OrderState {
        log("   cannot $action when $name")
        return this
    }

    data object Created : OrderState("Created") {
        override fun pay() = Paid
        override fun cancel() = Cancelled
    }
    data object Paid : OrderState("Paid") {
        override fun ship() = Shipped
        override fun cancel() = Cancelled
    }
    data object Shipped : OrderState("Shipped")
    data object Cancelled : OrderState("Cancelled")
}

fun main() {
    section("1. Observer")
    val pens = StockLevel(10)
    val stopBadge = pens.subscribe { log("badge shows $it") }
    pens.subscribe { if (it < 3) log("ALERT: only $it pens left") }
    pens.update(5)
    pens.update(5) // same value, nobody notified
    stopBadge()
    pens.update(2)
    note("After unsubscribing, the badge stopped receiving updates.")

    section("2. State")
    var order: OrderState = OrderState.Created
    log("state: ${order.name}")
    order = order.ship()
    order = order.pay()
    log("state: ${order.name}")
    order = order.ship()
    log("state: ${order.name}")
    order = order.cancel()
    log("state: ${order.name}")
    note("Each state object owns its rules. Adding OnHold = one new object, no edits elsewhere.")
}
