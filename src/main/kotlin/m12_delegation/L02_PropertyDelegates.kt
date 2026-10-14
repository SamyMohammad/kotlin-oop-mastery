package m12_delegation

import common.log
import common.note
import common.section
import kotlin.properties.Delegates

/*
 * L02 - Standard property delegates
 *
 *  val x by lazy { ... }                       compute once on first read
 *  var x by Delegates.observable(init) { }     callback AFTER every change
 *  var x by Delegates.vetoable(init) { }       callback BEFORE the change; return false to reject
 *  val x by map                                read the value from a Map by the property name
 */

class CheckoutForm {
    var quantity: Int by Delegates.vetoable(1) { _, _, new -> new in 1..10 }

    var address: String by Delegates.observable("") { prop, old, new ->
        log("  ${prop.name}: '$old' -> '$new'")
    }

    val shippingQuote: String by lazy {
        log("  calculating shipping quote...")
        "EGP 25.00"
    }
}

class ProductRow(fields: Map<String, Any?>) {
    val name: String by fields
    val price: Long by fields
}

fun main() {
    section("1. vetoable rejects bad values")
    val form = CheckoutForm()
    form.quantity = 3
    form.quantity = 50
    form.quantity = 0
    log("quantity = ${form.quantity}")

    section("2. observable reports every change")
    form.address = "Maadi"
    form.address = "Maadi, Street 9"

    section("3. lazy")
    log("quote = ${form.shippingQuote}")
    log("quote = ${form.shippingQuote}")

    section("4. Delegating to a Map")
    val row = ProductRow(mapOf("name" to "Pen", "price" to 500L))
    log("${row.name} costs ${row.price}")
    note("Useful for JSON-like data. A missing key throws NoSuchElementException on read.")
}
