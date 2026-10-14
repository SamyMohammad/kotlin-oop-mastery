package m00_mental_model

import common.log
import common.note
import common.section

/*
 * L01 - Procedural vs object-oriented
 *
 * The same shopping cart, written twice:
 *   (a) procedural: data in one place, functions somewhere else, anyone can touch the data
 *   (b) object:     the Cart owns its data AND the rules that protect it
 */

// ---------- (a) procedural: a loose list + free functions ----------

fun addLine(lines: MutableList<Pair<String, Long>>, name: String, priceCents: Long) {
    lines.add(name to priceCents) // no rule stops a negative price
}

fun totalOf(lines: List<Pair<String, Long>>): Long = lines.sumOf { it.second }

// ---------- (b) object: data + behaviour + rules in one unit ----------

class Cart {
    // private: nobody outside the class can reach the list directly
    private val lines = mutableListOf<Pair<String, Long>>()

    fun add(name: String, priceCents: Long) {
        require(priceCents >= 0) { "price must not be negative" }
        lines.add(name to priceCents)
    }

    fun total(): Long = lines.sumOf { it.second }

    fun size(): Int = lines.size
}

fun main() {
    section("1. Same result, two styles")
    val loose = mutableListOf<Pair<String, Long>>()
    addLine(loose, "Pen", 500)
    addLine(loose, "Mug", 3000)
    log("procedural total = ${totalOf(loose)}")

    val cart = Cart()
    cart.add("Pen", 500)
    cart.add("Mug", 3000)
    log("object total     = ${cart.total()}")
    note("Same numbers. The difference shows up when someone makes a mistake.")

    section("2. A bad price")
    addLine(loose, "Refund hack", -3500)
    log("procedural total = ${totalOf(loose)}   <- negative price accepted, money vanished")
    loose.clear() // anyone can also wipe the data
    log("procedural lines after someone called clear() = ${loose.size}")

    try {
        cart.add("Refund hack", -3500)
    } catch (e: IllegalArgumentException) {
        log("object refused: ${e.message}")
    }
    log("object total     = ${cart.total()}, lines = ${cart.size()}")
    note("The object protects its own rules. That is the whole point of OOP.")
}
