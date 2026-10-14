package m03_inheritance.exercises

import common.check
import common.runCheckSuite

/** Base type the learner subclasses. Do not change. */
open class Shipment(val weightGrams: Int) {
    init { require(weightGrams > 0) { "weight must be positive" } }
    /** Base price: 20 EGP (2000 cents) + 1 cent per gram. */
    open fun costCents(): Long = 2000L + weightGrams
    open fun label(): String = "Standard ${weightGrams}g"
}

interface M03Factory {
    /** Subclass: cost = base cost * 2. label = "Express ${weight}g". */
    fun express(weightGrams: Int): Shipment
    /** Subclass of the express subclass: cost = express cost + 5000. label = express label + " (fragile)". Must call super. */
    fun fragileExpress(weightGrams: Int): Shipment
    /** Subclass: free when weight <= 1000g, otherwise base cost. label unchanged from base. */
    fun freeSmall(weightGrams: Int): Shipment
}

fun runChecks(f: M03Factory) = runCheckSuite("m03 checks") {
    check("express doubles cost") { f.express(1000).costCents() == 6000L }
    check("express label") { f.express(500).label() == "Express 500g" }
    check("fragile builds on express") { f.fragileExpress(1000).costCents() == 11000L }
    check("fragile label via super") { f.fragileExpress(10).label() == "Express 10g (fragile)" }
    check("free small is free") { f.freeSmall(1000).costCents() == 0L }
    check("free small falls back") { f.freeSmall(1001).costCents() == 3001L }
    check("free small keeps base label") { f.freeSmall(10).label() == "Standard 10g" }
    check("subclasses are Shipments") { listOf(f.express(1), f.freeSmall(1)).all { it is Shipment } }
    check("base validation still runs") {
        runCatching { f.express(0) }.exceptionOrNull() is IllegalArgumentException
    }
}
