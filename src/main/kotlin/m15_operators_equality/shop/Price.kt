package m15_operators_equality.shop

/*
 * Shop snapshot for module 15: Price with a full set of operators.
 * Comparable gives <, >, <=, >= and sorting for free.
 */
@JvmInline
value class Price(val cents: Long) : Comparable<Price> {
    operator fun plus(other: Price) = Price(cents + other.cents)
    operator fun minus(other: Price) = Price(cents - other.cents)
    operator fun times(qty: Int) = Price(cents * qty)
    operator fun unaryMinus() = Price(-cents)
    override operator fun compareTo(other: Price) = cents.compareTo(other.cents)

    override fun toString() = "EGP %d.%02d".format(cents / 100, cents % 100)
}
