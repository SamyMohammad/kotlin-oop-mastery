package m04_abstract

import common.log
import common.note
import common.section
import m04_abstract.shop.Discount
import m04_abstract.shop.FixedDiscount
import m04_abstract.shop.PercentDiscount

/*
 * L01 - Abstract classes and abstract members
 *
 *  abstract class   : cannot be instantiated, is open automatically
 *  abstract member  : no body, every concrete subclass MUST implement it
 *  concrete member  : shared code, works for all subclasses
 *  constructor      : abstract classes can have one and hold state
 *
 *  Discount("X")  <- error: Cannot create an instance of an abstract class.
 */

abstract class Product(val name: String) {
    abstract val category: String                // abstract property
    abstract fun shippingCents(): Long           // abstract function

    fun summary() = "$name [$category] ships for ${shippingCents()}" // concrete
}

class Book(name: String, private val pages: Int) : Product(name) {
    override val category = "books"
    override fun shippingCents(): Long = if (pages > 500) 3000 else 2000
}

class EBook(name: String) : Product(name) {
    override val category = "digital"
    override fun shippingCents(): Long = 0
}

fun main() {
    section("1. Abstract members, concrete summary()")
    val items: List<Product> = listOf(Book("Kotlin in Depth", 620), Book("Clean Code", 430), EBook("Compose Notes"))
    for (p in items) log(p.summary())
    note("summary() is written once; each subclass only fills the abstract parts.")

    section("2. The shop Discount")
    val subtotal = 20_000L
    val discounts: List<Discount> = listOf(PercentDiscount("SAVE10", 10), FixedDiscount("MINUS50", 5_000), FixedDiscount("HUGE", 99_999))
    for (d in discounts) log("${d.code}: $subtotal -> ${d.apply(subtotal)}")
    note("apply() lives in the abstract base, so 'never below zero' can't be forgotten.")
}
