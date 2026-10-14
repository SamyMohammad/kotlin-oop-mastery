package m03_inheritance.exercises

/*
 * Module 03 exercises. Extend Shipment (in Checks.kt) - do not edit it.
 *
 * Tip:
 *   open class ExpressShipment(weight: Int) : Shipment(weight) { override fun ... }
 *   class FragileExpress(weight: Int) : ExpressShipment(weight) { ... super.costCents() ... }
 *   class FreeSmallShipment(weight: Int) : Shipment(weight) { ... }
 */

object MyFactory : M03Factory {
    /** Cost doubles the base cost; label "Express <w>g". Must be `open` so fragile can extend it. */
    override fun express(weightGrams: Int): Shipment = TODO()

    /** Extends YOUR express class: +5000 cents and " (fragile)" appended, both via super. */
    override fun fragileExpress(weightGrams: Int): Shipment = TODO()

    /** Free up to 1000g, otherwise super.costCents(). Do not override label(). */
    override fun freeSmall(weightGrams: Int): Shipment = TODO()
}

fun main() = runChecks(MyFactory)
