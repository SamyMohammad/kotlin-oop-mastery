package m09_object_companion

import common.log
import common.note
import common.section
import m09_object_companion.shop.Product
import m09_object_companion.shop.sample

/*
 * L03 - Factories and constants
 *
 * Private constructor + companion factory:
 *   - the factory can validate and return null / Result instead of throwing
 *   - it can count, cache, or return a subclass
 *
 * const val vs val:
 *   const val  compile-time constant, inlined at use sites (primitives and String only)
 *   val        computed at runtime when the companion is initialized
 *
 * Extensions can be declared on a companion:  fun Product.Companion.sample()
 */

class Settings {
    companion object {
        const val MAX_CART_ITEMS = 50          // compile-time constant
        val startedAt = "boot-" + (40 + 2)     // runtime value
        // const val bad = listOf(1)           <- error: Const 'val' has type 'List<Int>'. Only primitives and String are allowed
    }
}

fun main() {
    section("1. Factory with validation")
    val ok = Product.create("KS-101", "Pen", 500)
    val badSku = Product.create("XX-1", "Pen", 500)
    val badPrice = Product.create("KS-102", "Mug", -1)
    log("ok       = $ok")
    log("badSku   = $badSku")
    log("badPrice = $badPrice")
    log("created so far = ${Product.createdCount()}")
    note("Invalid input gives null instead of a half-built object or an exception.")

    section("2. Extension on the companion")
    log(Product.sample())

    section("3. const val vs val")
    log("MAX_CART_ITEMS = ${Settings.MAX_CART_ITEMS}")
    log("startedAt      = ${Settings.startedAt}")
}
