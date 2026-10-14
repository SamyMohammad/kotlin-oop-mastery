package m05_interfaces

import common.log
import common.note
import common.section
import m05_interfaces.shop.PickupPoint
import m05_interfaces.shop.ShippingMethod
import m05_interfaces.shop.StandardShipping

/*
 * L01 - Interface basics
 *
 * An interface is a CONTRACT: "anything of this type can do X".
 * A class can implement many interfaces (but extend only one class).
 */

interface Printable {
    fun printLabel(): String
}

interface Trackable {
    fun trackingUrl(): String
}

// One class, two capabilities
class Parcel(private val id: String) : Printable, Trackable {
    override fun printLabel() = "PARCEL #$id"
    override fun trackingUrl() = "https://track.example/$id"
}

class Invoice(private val number: Int) : Printable {
    override fun printLabel() = "INVOICE $number"
}

fun printAll(items: List<Printable>) {
    for (item in items) log(item.printLabel())
}

fun main() {
    section("1. Interface as a type")
    printAll(listOf(Parcel("A1"), Invoice(1001)))
    note("printAll() knows only Printable - it accepts parcels, invoices, anything printable.")

    section("2. One class, many interfaces")
    val parcel = Parcel("B7")
    val asTrackable: Trackable = parcel
    log(parcel.printLabel())
    log(asTrackable.trackingUrl())

    section("3. Shop shipping methods")
    val methods: List<ShippingMethod> = listOf(StandardShipping(), PickupPoint("Maadi"))
    for (m in methods) log("${m.name}: ${m.costCents(1500)} cents, ${m.eta()}")
    note("Checkout loops over ShippingMethod without knowing any concrete class.")
}
