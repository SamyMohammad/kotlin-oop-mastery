package m11_nested_inner_anonymous

import common.log
import common.note
import common.section

/*
 * L01 - Nested vs inner classes
 *
 *  class Outer { class Nested }        Nested has NO reference to an Outer instance
 *                                      (like `static class` in Java). This is the Kotlin default.
 *  class Outer { inner class Inner }   Inner holds a hidden reference to its Outer instance
 *                                      (like a non-static inner class in Java). Access it with this@Outer.
 *
 * Memory: an inner object keeps its outer object alive. On Android, an inner
 * listener inside an Activity keeps the whole Activity in memory -> leak.
 */

class Store(val name: String) {
    private var visits = 0

    // Nested: independent helper, cannot see `name` or `visits`
    class Address(val city: String) {
        fun label() = "Address in $city"
        // fun storeName() = name   <- error: Unresolved reference 'name'
    }

    // Inner: bound to one Store
    inner class Checkout(val counter: Int) {
        fun open(): String {
            visits++ // can touch the outer object's private state
            return "${this@Store.name} counter $counter (visit #$visits)"
        }
    }
}

fun main() {
    section("1. Nested class: no outer instance needed")
    val address = Store.Address("Cairo")
    log(address.label())

    section("2. Inner class: needs an outer instance")
    val maadi = Store("Maadi branch")
    val c1 = maadi.Checkout(1)
    val c2 = maadi.Checkout(2)
    log(c1.open())
    log(c2.open())
    log(c1.open())
    note("Both checkouts share the same Store, so visits keeps counting across them.")

    section("3. Different outer, different state")
    val zamalek = Store("Zamalek branch")
    log(zamalek.Checkout(1).open())
    note("Prefer nested (the default). Use inner only when you really need the outer object.")
}
