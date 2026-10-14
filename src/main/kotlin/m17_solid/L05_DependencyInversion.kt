package m17_solid

import common.log
import common.note
import common.section

/*
 * L05 - D: Dependency Inversion Principle
 *
 * "High-level code should not depend on low-level details. Both depend on abstractions."
 *
 * BEFORE: CheckoutService creates MySqlOrders() inside itself - welded to a database.
 * AFTER:  CheckoutService asks for an OrderStore in its constructor (dependency injection).
 *         Production passes the real store; tests pass a fake.
 */

// ---------- BEFORE ----------
class MySqlOrders {
    fun insert(id: Int) = "MySQL INSERT order $id"
}

class CheckoutServiceBad {
    private val orders = MySqlOrders() // hard-wired: cannot be replaced, cannot be tested offline
    fun checkout(id: Int) = orders.insert(id)
}

// ---------- AFTER ----------
interface OrderStore {
    fun save(id: Int): String
}

class MySqlOrderStore : OrderStore {
    override fun save(id: Int) = "MySQL INSERT order $id"
}

class FakeOrderStore : OrderStore {
    val saved = mutableListOf<Int>()
    override fun save(id: Int): String {
        saved += id
        return "fake saved order $id"
    }
}

class CheckoutService(private val store: OrderStore) { // depends on the abstraction
    fun checkout(id: Int) = store.save(id)
}

fun main() {
    section("BEFORE: welded to MySQL")
    log(CheckoutServiceBad().checkout(1))

    section("AFTER: the store is injected")
    log(CheckoutService(MySqlOrderStore()).checkout(2))

    val fake = FakeOrderStore()
    val underTest = CheckoutService(fake)
    log(underTest.checkout(3))
    log("fake recorded: ${fake.saved}")
    note("The arrow of dependency now points at OrderStore - from both sides.")
}
