package m17_solid.exercises.solutions

import m17_solid.exercises.Checkout
import m17_solid.exercises.DiscountPolicy
import m17_solid.exercises.M17Factory
import m17_solid.exercises.Notifier
import m17_solid.exercises.OrderStore
import m17_solid.exercises.runChecks

/*
 * SRP: this class only orchestrates; pricing rules, storage and messaging live elsewhere.
 * OCP: new discounts are new DiscountPolicy values - this class never changes.
 * DIP: depends on three abstractions, all passed in.
 */
class StandardCheckout(
    private val store: OrderStore,
    private val notifier: Notifier,
    private val policy: DiscountPolicy,
) : Checkout {
    override fun place(id: Int, lineCents: List<Long>): Long {
        require(lineCents.isNotEmpty()) { "empty order" }
        val subtotal = lineCents.sum()
        val total = (subtotal - policy.discount(subtotal)).coerceAtLeast(0)
        store.save(id, total)
        notifier.notify("Order $id: $total")
        return total
    }
}

object FactorySolution : M17Factory {
    override fun checkout(store: OrderStore, notifier: Notifier, policy: DiscountPolicy): Checkout =
        StandardCheckout(store, notifier, policy)

    override fun noDiscount() = DiscountPolicy { 0 }

    override fun overThousandGets100Off() = DiscountPolicy { subtotal -> if (subtotal > 100_000) 10_000 else 0 }
}

fun main() = runChecks(FactorySolution)
