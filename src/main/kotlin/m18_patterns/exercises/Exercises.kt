package m18_patterns.exercises

/*
 * Module 18 exercises: State, Decorator, Adapter, Observer.
 *
 * State tip: since this file is in the same package as OrderState you may even
 * make your own `sealed interface MyState : OrderState` here. Each state object
 * decides its next state:
 *   object Created : MyState { override val name = "Created"; fun pay() = Paid; ... }
 *
 * Decorator tip: class Vat(private val inner: PriceSource) : PriceSource { ... inner.priceCents() ... }
 * Adapter tip:   class UsdPrice(private val fx: LegacyFx, private val usd: Double) : PriceSource
 * Observer tip:  keep a MutableList<(T) -> Unit>; subscribe adds and returns { list.remove(listener) }
 */

object MyFactory : M18Factory {
    override fun machine(): OrderMachine = TODO()
    override fun withVat(source: PriceSource): PriceSource = TODO()
    override fun withCoupon(source: PriceSource, offCents: Long): PriceSource = TODO()
    override fun fromUsd(fx: LegacyFx, usd: Double): PriceSource = TODO()
    override fun <T> observable(): Pair<((T) -> Unit) -> (() -> Unit), (T) -> Unit> = TODO()
}

fun main() = runChecks(MyFactory)
