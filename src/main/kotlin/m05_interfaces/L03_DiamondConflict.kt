package m05_interfaces

import common.log
import common.note
import common.section

/*
 * L03 - The diamond: two interfaces, same default method
 *
 * If a class inherits two implementations of the same member, Kotlin refuses
 * to guess. You must override it and may call each one with super<Name>.
 *   "Class 'ShopItem' must override public open fun describe(): String
 *    because it inherits multiple interface methods of it"
 */

interface Shippable {
    fun describe(): String = "ships in a box"
}

interface Downloadable {
    fun describe(): String = "downloads instantly"
}

class Bundle : Shippable, Downloadable {
    override fun describe(): String =
        "bundle: " + super<Shippable>.describe() + " + " + super<Downloadable>.describe()
}

class PhysicalOnly : Shippable, Downloadable {
    // Choosing one side is also fine
    override fun describe(): String = super<Shippable>.describe()
}

fun main() {
    section("1. Combine both")
    log(Bundle().describe())

    section("2. Pick one")
    log(PhysicalOnly().describe())

    section("3. Called through each interface type")
    val b = Bundle()
    val s: Shippable = b
    val d: Downloadable = b
    log("as Shippable:    ${s.describe()}")
    log("as Downloadable: ${d.describe()}")
    note("Same object, same override - the static type does not matter for members.")
}
