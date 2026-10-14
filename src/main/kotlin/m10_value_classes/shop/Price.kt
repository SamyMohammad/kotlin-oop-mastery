package m10_value_classes.shop

/*
 * Shop snapshot for module 10: typed wrappers that cost nothing at runtime.
 * A Price can never be passed where a ProductId or Quantity is expected.
 */
@JvmInline
value class Price(val cents: Long) {
    init {
        require(cents >= 0) { "price must not be negative" }
    }

    operator fun plus(other: Price) = Price(cents + other.cents)
    operator fun times(qty: Quantity) = Price(cents * qty.value)

    fun format() = "EGP %d.%02d".format(cents / 100, cents % 100)
}

@JvmInline
value class ProductId(val value: Int)

@JvmInline
value class Quantity(val value: Int) {
    init {
        require(value > 0) { "quantity must be positive" }
    }
}
