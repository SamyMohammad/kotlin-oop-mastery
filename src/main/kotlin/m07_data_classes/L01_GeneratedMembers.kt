package m07_data_classes

import common.log
import common.note
import common.section
import m07_data_classes.shop.Product

/*
 * L01 - What `data` generates
 *
 * For every property in the PRIMARY constructor, a data class gets:
 *   equals()       compares those properties (structural equality)
 *   hashCode()     consistent with equals
 *   toString()     "Product(id=1, name=Pen, ...)"
 *   componentN()   for destructuring:  val (id, name) = product
 *   copy()         a new object with some properties changed
 */

class PlainPoint(val x: Int, val y: Int)
data class DataPoint(val x: Int, val y: Int)

fun same(a: Any, b: Any) = if (a == b) "same" else "different"

fun main() {
    section("1. equals: plain class vs data class")
    log("PlainPoint(1,2) vs PlainPoint(1,2): ${same(PlainPoint(1, 2), PlainPoint(1, 2))}")
    log("DataPoint(1,2)  vs DataPoint(1,2):  ${same(DataPoint(1, 2), DataPoint(1, 2))}")

    section("2. toString")
    log(DataPoint(3, 4))
    log(Product(1, "Pen", 500))
    log("plain toString starts with class name + @: ${PlainPoint(3, 4).toString().contains("@")}")

    section("3. hashCode -> sets and maps work by value")
    val plainSet = setOf(PlainPoint(1, 1), PlainPoint(1, 1))
    val dataSet = setOf(DataPoint(1, 1), DataPoint(1, 1))
    log("plain set size = ${plainSet.size}")
    log("data set size  = ${dataSet.size}")

    section("4. Destructuring with componentN")
    val (id, name, price) = Product(7, "Mug", 4500)
    log("id=$id name=$name price=$price")
    val cart = listOf(Product(1, "Pen", 500), Product(2, "Cap", 9000))
    for ((_, n, p) in cart) log("$n costs $p")
    note("Destructuring is by POSITION, not by name. Reordering properties changes meaning.")
}
