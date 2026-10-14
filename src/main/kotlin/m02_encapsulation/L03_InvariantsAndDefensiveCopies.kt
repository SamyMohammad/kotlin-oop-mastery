package m02_encapsulation

import common.log
import common.note
import common.section
import m02_encapsulation.shop.Cart

/*
 * L03 - Invariants and defensive copies
 *
 * An invariant is a rule that must ALWAYS be true for an object, e.g.
 *   "every cart line has qty > 0" and "a product appears at most once".
 * Encapsulation exists to protect invariants - private is just the tool.
 */

class Wishlist(initial: List<String>) {
    // Defensive copy IN: later changes to the caller's list cannot reach us
    private val names: MutableList<String> = initial.toMutableList()

    fun add(name: String) {
        if (name.isNotBlank() && name !in names) names += name
    }

    // Defensive copy OUT
    fun all(): List<String> = names.toList()
}

fun main() {
    section("1. Cart protects its invariants")
    val cart = Cart()
    cart.add(productId = 1, qty = 2)
    cart.add(productId = 1, qty = 3) // merged, not duplicated
    cart.add(productId = 2, qty = 1)
    log("items = ${cart.items}")
    log("total qty = ${cart.totalQty}")
    try {
        cart.add(productId = 3, qty = 0)
    } catch (e: IllegalArgumentException) {
        log("rejected: ${e.message}")
    }

    section("2. Defensive copy on the way in")
    val source = mutableListOf("Mug", "Pen")
    val wishlist = Wishlist(source)
    source.clear() // caller changes their own list later
    log("caller's list = $source")
    log("wishlist      = ${wishlist.all()}")

    section("3. The object decides what is valid")
    wishlist.add("Mug")   // duplicate, ignored
    wishlist.add("   ")   // blank, ignored
    wishlist.add("Cap")
    log("wishlist      = ${wishlist.all()}")
    note("Encapsulation = keeping the rules true, no matter what callers do.")
}
