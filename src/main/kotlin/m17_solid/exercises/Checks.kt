package m17_solid.exercises

import common.check
import common.runCheckSuite

/** Abstractions the refactor must use (DIP + ISP). */
interface OrderStore { fun save(id: Int, totalCents: Long); fun all(): List<Pair<Int, Long>> }
interface Notifier { fun notify(message: String) }
fun interface DiscountPolicy { fun discount(subtotalCents: Long): Long }

interface Checkout { fun place(id: Int, lineCents: List<Long>): Long }

interface M17Factory {
    /** Build a checkout with injected dependencies (no `new` of concrete stores inside).
     *  place(): subtotal = sum(lineCents); empty lines -> IllegalArgumentException;
     *  total = subtotal - policy.discount(subtotal) floored at 0; store.save(id, total);
     *  notifier.notify("Order <id>: <total>"); returns total. */
    fun checkout(store: OrderStore, notifier: Notifier, policy: DiscountPolicy): Checkout
    /** Policies added WITHOUT modifying checkout (OCP). */
    fun noDiscount(): DiscountPolicy
    fun overThousandGets100Off(): DiscountPolicy  // subtotal > 100_000 -> 10_000 off, else 0
}

class FakeStore : OrderStore {
    val saved = mutableListOf<Pair<Int, Long>>()
    override fun save(id: Int, totalCents: Long) { saved += id to totalCents }
    override fun all() = saved.toList()
}
class FakeNotifier : Notifier { val sent = mutableListOf<String>(); override fun notify(message: String) { sent += message } }

fun runChecks(f: M17Factory) = runCheckSuite("m17 checks") {
    check("places and saves") {
        val s = FakeStore(); val n = FakeNotifier()
        f.checkout(s, n, f.noDiscount()).place(1, listOf(500, 700)) == 1200L && s.saved == listOf(1 to 1200L)
    }
    check("notifies") {
        val n = FakeNotifier(); f.checkout(FakeStore(), n, f.noDiscount()).place(7, listOf(100))
        n.sent == listOf("Order 7: 100")
    }
    check("policy applied") { f.checkout(FakeStore(), FakeNotifier(), f.overThousandGets100Off()).place(1, listOf(150_000)) == 140_000L }
    check("policy threshold") { f.checkout(FakeStore(), FakeNotifier(), f.overThousandGets100Off()).place(1, listOf(100_000)) == 100_000L }
    check("custom policy without touching checkout") {
        f.checkout(FakeStore(), FakeNotifier(), DiscountPolicy { it * 2 }).place(1, listOf(10)) == 0L
    }
    check("empty order rejected") {
        runCatching { f.checkout(FakeStore(), FakeNotifier(), f.noDiscount()).place(1, emptyList()) }
            .exceptionOrNull() is IllegalArgumentException
    }
}
