package m05_interfaces.shop

/*
 * Shop snapshot for module 05: shipping methods behind one interface.
 * Checkout code depends on ShippingMethod only, never on a concrete class.
 */
interface ShippingMethod {
    val name: String
    fun costCents(weightGrams: Int): Long
    fun eta(): String = "3-5 days" // default, may be overridden
}

class StandardShipping : ShippingMethod {
    override val name = "Standard"
    override fun costCents(weightGrams: Int): Long = 2000L + weightGrams / 10
}

class PickupPoint(private val pointName: String) : ShippingMethod {
    override val name get() = "Pickup at $pointName"
    override fun costCents(weightGrams: Int): Long = 500
    override fun eta(): String = "next day"
}
