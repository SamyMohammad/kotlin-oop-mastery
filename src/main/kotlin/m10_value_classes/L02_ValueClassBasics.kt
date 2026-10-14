package m10_value_classes

import common.log
import common.note
import common.section
import m10_value_classes.shop.Price
import m10_value_classes.shop.ProductId
import m10_value_classes.shop.Quantity

/*
 * L02 - Value classes
 *
 *  @JvmInline value class ProductId(val value: Int)
 *
 *  - exactly ONE property in the primary constructor, and it is a `val`
 *  - may have init blocks, functions, operators, and implement interfaces
 *  - no identity: equality is by the wrapped value, === is not allowed
 *  - at runtime it is usually just the underlying Int/Long (no object allocated)
 */

interface Labeled {
    fun label(): String
}

@JvmInline
value class Sku(val code: String) : Labeled {
    override fun label() = "SKU[$code]"
}

class TypedCart {
    private val lines = linkedMapOf<ProductId, Int>()

    fun add(productId: ProductId, qty: Quantity) {
        lines[productId] = (lines[productId] ?: 0) + qty.value
    }

    override fun toString() = lines.entries.joinToString { "product ${it.key.value} x${it.value}" }
}

fun main() {
    section("1. Swapping no longer compiles")
    val cart = TypedCart()
    cart.add(ProductId(1042), Quantity(2))
    // cart.add(Quantity(2), ProductId(1042))
    //   <- error: Argument type mismatch: actual type is 'Quantity', but 'ProductId' was expected.
    log(cart)

    section("2. Validation in init")
    try {
        Quantity(0)
    } catch (e: IllegalArgumentException) {
        log("Quantity(0) rejected: ${e.message}")
    }

    section("3. Operators on a value class")
    val unit = Price(4500)
    val total = unit * Quantity(3) + Price(1000)
    log("4500 x 3 + 1000 = ${total.format()}")

    section("4. Interfaces")
    val sku: Labeled = Sku("KS-77")
    log(sku.label())
    note("Used through an interface, the value class gets boxed - see L03.")
}
