package m11_nested_inner_anonymous

import common.log
import common.note
import common.section

/*
 * L02 - Object expressions (anonymous objects)
 *
 *  object : SomeInterface { ... }   creates ONE object of a new unnamed class, right here.
 *  - can implement interfaces and extend a class
 *  - can implement several interfaces at once
 *  - can hold its own state
 *  - extra members are only visible when the type stays local/private
 */

data class Item(val name: String, val priceCents: Long)

interface Discount {
    fun apply(cents: Long): Long
}

interface Describable {
    fun describe(): String
}

fun main() {
    section("1. Anonymous Comparator")
    val items = listOf(Item("Mug", 4500), Item("Pen", 500), Item("Cap", 9000))
    val byPrice = object : Comparator<Item> {
        override fun compare(a: Item, b: Item) = a.priceCents.compareTo(b.priceCents)
    }
    log(items.sortedWith(byPrice).map { it.name })

    section("2. One object, two interfaces")
    val promo = object : Discount, Describable {
        override fun apply(cents: Long) = cents - 1000
        override fun describe() = "1000 off"
    }
    log("${promo.describe()}: 4500 -> ${promo.apply(4500)}")

    section("3. Extra members on a local anonymous object")
    val tracker = object {
        var hits = 0
        fun hit() = ++hits
    }
    tracker.hit()
    tracker.hit()
    log("hits = ${tracker.hits}")
    note("Inside this function `tracker` keeps its anonymous type, so hits is visible.")
    note("Returned from a public function it would become plain Any.")

    section("4. Object expression vs lambda")
    val viaLambda = Comparator<Item> { a, b -> a.name.compareTo(b.name) }
    log(items.sortedWith(viaLambda).map { it.name })
    note("For a single-method interface a lambda is shorter. Use `object :` when you need state or several members.")
}
