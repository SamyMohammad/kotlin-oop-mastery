package m18_patterns

import common.log
import common.note
import common.section

/*
 * L04 - Decorator, Adapter (and a word on Repository)
 *
 * DECORATOR: wraps an object of the SAME interface and adds behaviour before/after.
 *            Stack them in any order. (Compose's Modifier chain works this way.)
 * ADAPTER:   wraps an object with a DIFFERENT interface so it fits the one you need.
 *            Typical for legacy or third-party APIs.
 * REPOSITORY (m13): hides where data comes from behind a collection-like interface.
 */

interface PriceCalculator {
    fun price(baseCents: Long): Long
    fun describe(): String
}

class BasePrice : PriceCalculator {
    override fun price(baseCents: Long) = baseCents
    override fun describe() = "base"
}

class DiscountDecorator(private val inner: PriceCalculator, private val percent: Int) : PriceCalculator {
    override fun price(baseCents: Long) = inner.price(baseCents) * (100 - percent) / 100
    override fun describe() = "${inner.describe()} -> $percent% off"
}

class TaxDecorator(private val inner: PriceCalculator) : PriceCalculator {
    override fun price(baseCents: Long) = inner.price(baseCents) * 114 / 100
    override fun describe() = "${inner.describe()} -> +14% VAT"
}

// Adapter: our checkout needs ShippingMethod (cents, grams)...
interface ShippingMethod {
    fun costCents(weightGrams: Int): Long
}

// ...but the courier's old SDK speaks kilograms and pounds (EGP as Double)
class OldCourierSdk {
    fun calc(kg: Double): Double = 20.0 + kg * 7.5
}

class CourierAdapter(private val sdk: OldCourierSdk) : ShippingMethod {
    override fun costCents(weightGrams: Int): Long = (sdk.calc(weightGrams / 1000.0) * 100).toLong()
}

fun main() {
    section("1. Decorators stack in any order")
    val a = TaxDecorator(DiscountDecorator(BasePrice(), 10))
    val b = DiscountDecorator(TaxDecorator(BasePrice()), 10)
    log("${a.describe()}: ${a.price(10_000)}")
    log("${b.describe()}: ${b.price(10_000)}")
    note("Same pieces, different order. Here the result happens to match; with fixed-amount coupons it would not.")

    section("2. Adapter")
    val shipping: ShippingMethod = CourierAdapter(OldCourierSdk())
    log("2000g via courier = ${shipping.costCents(2000)} cents")
    note("Checkout code only knows ShippingMethod. The old SDK stays hidden behind the adapter.")
}
