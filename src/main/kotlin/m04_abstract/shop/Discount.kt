package m04_abstract.shop

/*
 * Shop snapshot for module 04: discounts as an abstract class.
 * The shared rule (never below zero) lives in the base; the amount is abstract.
 */
abstract class Discount(val code: String) {
    abstract fun amountOff(subtotalCents: Long): Long

    fun apply(subtotalCents: Long): Long = (subtotalCents - amountOff(subtotalCents)).coerceAtLeast(0)
}

class PercentDiscount(code: String, private val percent: Int) : Discount(code) {
    init {
        require(percent in 1..100) { "percent must be 1..100" }
    }

    override fun amountOff(subtotalCents: Long): Long = subtotalCents * percent / 100
}

class FixedDiscount(code: String, private val offCents: Long) : Discount(code) {
    override fun amountOff(subtotalCents: Long): Long = offCents
}
