package m15_operators_equality

import common.log
import common.note
import common.section
import m15_operators_equality.shop.Price

/*
 * L01 - Operator overloading
 *
 * Kotlin maps operators to functions with fixed names, marked `operator`:
 *   a + b   -> a.plus(b)          a - b  -> a.minus(b)     a * b -> a.times(b)
 *   -a      -> a.unaryMinus()     a < b  -> a.compareTo(b) < 0
 *   a += b  -> a.plusAssign(b) if it exists, otherwise a = a.plus(b) (for a var)
 */

class Basket {
    val items = mutableListOf<String>()
    operator fun plusAssign(item: String) { // mutate in place
        items += item
    }
}

fun main() {
    section("1. Arithmetic on Price")
    val pen = Price(500)
    val mug = Price(4500)
    log("pen + mug   = ${pen + mug}")
    log("mug - pen   = ${mug - pen}")
    log("pen * 3     = ${pen * 3}")
    log("-pen        = ${-pen}")

    section("2. Comparison through compareTo")
    log("pen < mug   = ${pen < mug}")
    log("max         = ${maxOf(pen, mug)}")
    log("sorted      = ${listOf(mug, Price(100), pen).sorted()}")

    section("3. += two ways")
    var total = Price(0)
    total += pen        // no plusAssign on Price: becomes total = total + pen (needs var)
    total += mug
    log("total       = $total")

    val basket = Basket()
    basket += "Pen"     // plusAssign: mutates the same basket (val is fine)
    basket += "Mug"
    log("basket      = ${basket.items}")
    note("Value types: plus returns a new value. Containers: plusAssign mutates.")
}
