package m05_interfaces

import common.log
import common.note
import common.section

/*
 * L02 - Default methods and interface properties
 *
 *  - fun with a body           = default method (implementers may override)
 *  - val without a body        = abstract property (implementers must provide)
 *  - val with a getter         = computed property (allowed)
 *  - val x = 5                 = NOT allowed: interfaces cannot hold state
 *                                ("Property initializers are not allowed in interfaces")
 */

interface PricedItem {
    val name: String               // abstract property
    val priceCents: Long           // abstract property

    val isFree: Boolean            // computed property: getter, no field
        get() = priceCents == 0L

    fun label(): String = if (isFree) "$name (free)" else "$name - $priceCents" // default method
}

class Mug : PricedItem {
    override val name = "Mug"
    override val priceCents = 4500L
}

class Sticker : PricedItem {
    override val name = "Sticker"
    override val priceCents = 0L
}

class GiftCard(override val priceCents: Long) : PricedItem { // property via constructor
    override val name = "Gift card"
    override fun label() = "$name worth $priceCents" // override the default
}

fun main() {
    section("1. Using the defaults")
    log(Mug().label())
    log(Sticker().label())

    section("2. Overriding a default")
    log(GiftCard(10_000).label())

    section("3. Computed interface property")
    for (item in listOf(Mug(), Sticker(), GiftCard(0))) log("${item.name}: isFree=${item.isFree}")
    note("isFree is computed from priceCents. No field lives in the interface.")
}
