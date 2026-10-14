package m07_data_classes

import common.log
import common.note
import common.section
import m07_data_classes.shop.Product

/*
 * L02 - copy() and immutable state
 *
 * Immutable object: all properties are `val` and of immutable types.
 * To "change" it you create a new object with copy(...).
 * Old references keep seeing the old value - nobody is surprised.
 *
 * copy() is SHALLOW: it copies references, not the objects they point to.
 */

data class CartState(val items: List<String> = emptyList(), val couponCode: String? = null)

data class LeakyState(val items: MutableList<String>)

fun main() {
    section("1. copy with named arguments")
    val pen = Product(1, "Pen", 1000)
    val saleP = pen.discounted(20)
    val renamed = pen.copy(name = "Blue Pen")
    log("original: $pen")
    log("sale:     $saleP")
    log("renamed:  $renamed")

    section("2. A new object every time")
    val s1 = CartState()
    val s2 = s1.copy(items = s1.items + "Pen")
    val s3 = s2.copy(couponCode = "SAVE10")
    log("s1 = $s1")
    log("s2 = $s2")
    log("s3 = $s3")
    log("s2 and s3 same object? ${if (s2 === s3) "same" else "different"}")
    note("Each step is a snapshot. s1 is still empty - history is preserved.")

    section("3. The shallow copy trap")
    val original = LeakyState(mutableListOf("Pen"))
    val copied = original.copy()
    copied.items.add("Stolen TV")
    log("copied.items   = ${copied.items}")
    log("original.items = ${original.items}")
    log("same list object? ${if (original.items === copied.items) "same" else "different"}")
    note("copy() copied the REFERENCE. Use read-only List and build new lists instead.")
}
