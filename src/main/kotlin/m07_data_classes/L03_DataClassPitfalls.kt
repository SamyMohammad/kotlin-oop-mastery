package m07_data_classes

import common.log
import common.note
import common.section

/*
 * L03 - Data class pitfalls
 *
 * 1) Only PRIMARY-constructor properties take part in equals/hashCode/toString/copy.
 * 2) A `var` property used as a HashSet/HashMap key, then mutated, "disappears".
 * 3) data classes cannot be open, abstract, sealed or inner
 *    (they can implement interfaces and extend open classes).
 */

data class Order(val id: Int, val total: Long) {
    var note: String = "" // body property: invisible to equals/toString/copy
}

data class MutableKey(var code: String)

fun main() {
    section("1. Body properties are ignored")
    val a = Order(1, 500).apply { note = "gift wrap" }
    val b = Order(1, 500).apply { note = "leave at door" }
    log("a == b ? ${a == b}")
    log("a.toString() = $a")
    log("a.copy().note = '${a.copy().note}'")
    note("The note was lost by copy() and ignored by equals.")

    section("2. Mutating a key inside a HashSet")
    val key = MutableKey("SAVE10")
    val used = hashSetOf(key)
    log("contains before = ${used.contains(key)}")
    key.code = "SAVE20" // hashCode changes, but the set stored it under the old bucket
    log("contains after  = ${used.contains(key)}")
    log("set still has ${used.size} element(s)")
    note("Keep data classes immutable (val only), especially when used as keys.")

    section("3. What data classes cannot be")
    log("open data class X   -> error: modifier 'open' is incompatible with 'data'")
    log("data class Y : SomeInterface -> fine")
}
