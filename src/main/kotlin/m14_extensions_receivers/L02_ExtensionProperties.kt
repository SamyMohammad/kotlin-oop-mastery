package m14_extensions_receivers

import common.log
import common.note
import common.section

/*
 * L02 - Extension properties
 *
 *  val Product.isExpensive: Boolean get() = priceCents > 100_000
 *
 * An extension property is only a getter (and setter for var). It has NO
 * backing field, so it cannot store anything:
 *  val Product.cache = 5   <- error: Extension property cannot be initialized because it has no backing field
 */

data class Product(val name: String, val priceCents: Long, val stock: Int)

val Product.isExpensive: Boolean
    get() = priceCents > 100_000

val Product.stockLabel: String
    get() = when {
        stock == 0 -> "out of stock"
        stock < 5 -> "only $stock left"
        else -> "in stock"
    }

val List<Product>.totalValueCents: Long
    get() = sumOf { it.priceCents * it.stock }

fun main() {
    section("1. Computed extension properties")
    val items = listOf(
        Product("Pen", 500, 120),
        Product("Laptop", 2_500_000, 3),
        Product("Mug", 4500, 0),
    )
    for (p in items) log("${p.name}: expensive=${p.isExpensive}, ${p.stockLabel}")

    section("2. On a generic type")
    log("stock value = ${items.totalValueCents}")
    note("Extension properties read like fields but are computed each time.")
}
