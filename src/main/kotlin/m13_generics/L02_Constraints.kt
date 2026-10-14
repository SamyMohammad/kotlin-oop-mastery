package m13_generics

import common.log
import common.note
import common.section

/*
 * L02 - Constraints (upper bounds)
 *
 *  fun <T : Comparable<T>> maxOf(...)      T must be comparable to itself
 *  where T : A, T : B                      several bounds at once
 *
 * A bound lets you CALL the bound's members on T, and rejects types that don't qualify.
 */

fun <T : Comparable<T>> largest(items: List<T>): T? {
    var best: T? = null
    for (item in items) if (best == null || item > best) best = item // `>` needs Comparable
    return best
}

interface Named {
    val name: String
}

interface Priced {
    val priceCents: Long
}

data class Gadget(override val name: String, override val priceCents: Long) : Named, Priced

// Needs BOTH capabilities
fun <T> cheapestLabel(items: List<T>): String where T : Named, T : Priced {
    val cheapest = items.minByOrNull { it.priceCents } ?: return "none"
    return "${cheapest.name} (${cheapest.priceCents})"
}

fun main() {
    section("1. T : Comparable<T>")
    log(largest(listOf(3, 9, 2)))
    log(largest(listOf("pear", "apple", "zucchini")))
    log(largest(emptyList<Long>()))
    // largest(listOf(Gadget("x", 1)))  <- error: Gadget is not Comparable<Gadget>

    section("2. Multiple bounds with where")
    val gadgets = listOf(Gadget("Charger", 30_000), Gadget("Cable", 9_000), Gadget("Case", 15_000))
    log(cheapestLabel(gadgets))
    note("Inside cheapestLabel, T has both .name and .priceCents because of the bounds.")
}
