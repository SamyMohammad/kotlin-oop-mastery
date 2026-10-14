package m01_classes

import common.log
import common.note
import common.section

/*
 * L03 - Properties
 *
 *  - A Kotlin property = (optional) hidden field + getter (+ setter for var)
 *  - custom getter: computed every time it is read, no field stored
 *  - custom setter: `field` is the hidden backing field
 *  - private set: everyone reads, only the class writes
 *  - const val: compile-time constant (primitives and String only)
 */

class StockItem(val name: String, private val priceCents: Long) {

    var getterCalls = 0
        private set

    // Computed property: no backing field exists for it
    val priceLabel: String
        get() {
            getterCalls++
            return "EGP %d.%02d".format(priceCents / 100, priceCents % 100)
        }

    // Stored property with a guarded setter
    var stock: Int = 0
        set(value) {
            if (value < 0) {
                log("   rejected stock = $value")
                return
            }
            field = value // writing `stock = value` here would call the setter again forever
        }

    // Readable everywhere, writable only inside the class
    var soldCount: Int = 0
        private set

    fun sell(qty: Int) {
        if (qty in 1..stock) {
            stock -= qty
            soldCount += qty
        }
    }

    companion object {
        const val CURRENCY = "EGP" // inlined at compile time
    }
}

fun main() {
    section("1. Computed getter runs on every read")
    val mug = StockItem("Mug", 4550)
    log(mug.priceLabel)
    log(mug.priceLabel)
    log("getter ran ${mug.getterCalls} times")

    section("2. Setter with backing field")
    mug.stock = 10
    mug.stock = -4
    log("stock = ${mug.stock}")

    section("3. private set")
    mug.sell(3)
    log("stock = ${mug.stock}, sold = ${mug.soldCount}")
    // mug.soldCount = 100  <- does not compile: the setter is private
    note("The class keeps soldCount honest. Outside code can only read it.")

    section("4. const val")
    log("currency = ${StockItem.CURRENCY}")
}
