package m13_generics

import common.log
import common.note
import common.section
import m13_generics.shop.Customer
import m13_generics.shop.InMemoryRepository
import m13_generics.shop.Product

/*
 * L01 - Generic classes and functions
 *
 *  class Box<T>(val value: T)          T is a type parameter, filled in by each use
 *  fun <T> firstOrDefault(...)         generic function, T inferred from arguments
 *
 * One implementation, many types, full type safety - no casts.
 */

class Box<T>(val value: T) {
    fun <R> map(transform: (T) -> R): Box<R> = Box(transform(value))
    override fun toString() = "Box($value)"
}

// A tiny Result-like type
sealed interface Outcome<out T> {
    data class Ok<T>(val value: T) : Outcome<T>
    data class Failed(val reason: String) : Outcome<Nothing>
}

fun parsePrice(text: String): Outcome<Long> =
    text.toLongOrNull()?.let { Outcome.Ok(it) } ?: Outcome.Failed("not a number: $text")

fun <T> firstOrDefault(items: List<T>, default: T): T = items.firstOrNull() ?: default

fun main() {
    section("1. Generic class")
    val boxed = Box(4500L)
    val label: Box<String> = boxed.map { "EGP ${it / 100}" }
    log("$boxed -> $label")

    section("2. Type inference")
    log(firstOrDefault(listOf("Pen", "Mug"), "nothing"))
    log(firstOrDefault(emptyList<Int>(), -1))

    section("3. One repository, many entity types")
    val products = InMemoryRepository<Product>()
    val customers = InMemoryRepository<Customer>()
    products.save(Product(1, "Pen"))
    products.save(Product(2, "Mug"))
    customers.save(Customer(1, "sara@mail.com"))
    log("products  = ${products.all()}")
    log("customer1 = ${customers.byId(1)}")
    // products.save(Customer(3, "x"))  <- error: type mismatch, expected Product

    section("4. A generic result type")
    log(parsePrice("1250"))
    log(parsePrice("abc"))
    note("Failed is Outcome<Nothing>, and Nothing fits any Outcome<T> thanks to `out` (see L03).")
}
