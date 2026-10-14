package m03_inheritance.exercises.solutions

import m03_inheritance.exercises.M03Factory
import m03_inheritance.exercises.Shipment
import m03_inheritance.exercises.runChecks

// `open` so FragileExpress can extend it - Kotlin classes are final by default
open class ExpressShipment(weightGrams: Int) : Shipment(weightGrams) {
    override fun costCents(): Long = super.costCents() * 2
    override fun label(): String = "Express ${weightGrams}g"
}

class FragileExpress(weightGrams: Int) : ExpressShipment(weightGrams) {
    // super here is ExpressShipment, so we build on the express rules
    override fun costCents(): Long = super.costCents() + 5000
    override fun label(): String = super.label() + " (fragile)"
}

class FreeSmallShipment(weightGrams: Int) : Shipment(weightGrams) {
    override fun costCents(): Long = if (weightGrams <= 1000) 0L else super.costCents()
}

object FactorySolution : M03Factory {
    override fun express(weightGrams: Int): Shipment = ExpressShipment(weightGrams)
    override fun fragileExpress(weightGrams: Int): Shipment = FragileExpress(weightGrams)
    override fun freeSmall(weightGrams: Int): Shipment = FreeSmallShipment(weightGrams)
}

fun main() = runChecks(FactorySolution)
