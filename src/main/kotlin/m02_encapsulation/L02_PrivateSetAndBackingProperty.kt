package m02_encapsulation

import common.log
import common.note
import common.section

/*
 * L02 - private set and the backing property pattern
 *
 *  var x: T private set        -> read everywhere, write only inside
 *  private val _x = Mutable... -> the real, mutable storage
 *  val x: ReadOnlyType          -> the public face
 *
 * Trap: List is a READ-ONLY VIEW, not an immutable list. If you return the
 * same MutableList typed as List, a caller can cast it back and mutate it.
 */

class LeakyCart {
    private val _items = mutableListOf("Pen")
    val items: List<String> = _items // same object, just a narrower type
}

class SafeCart {
    private val _items = mutableListOf("Pen", "Mug")
    // New copy on every read. (For 0 or 1 items toList() returns an unmodifiable
    // list, so the attack below would throw instead - still safe.)
    val items: List<String> get() = _items.toList()
}

class Counter {
    var count: Int = 0
        private set

    fun increment() {
        count++
    }
}

fun main() {
    section("1. private set")
    val counter = Counter()
    counter.increment()
    counter.increment()
    log("count = ${counter.count}")
    // counter.count = 99  <- does not compile

    section("2. A read-only type is not a wall")
    val leaky = LeakyCart()
    @Suppress("UNCHECKED_CAST")
    (leaky.items as MutableList<String>).add("Stolen TV")
    log("leaky cart now has: ${leaky.items}")
    note("The cast worked because items IS the private MutableList.")

    section("3. Return a copy")
    val safe = SafeCart()
    val snapshot = safe.items
    @Suppress("UNCHECKED_CAST")
    (snapshot as MutableList<String>).add("Stolen TV")
    log("snapshot the caller changed: $snapshot")
    log("safe cart still has:         ${safe.items}")
    note("toList() hands out a new list. Mutating it cannot reach the cart.")
}
