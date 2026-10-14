package m01_classes.exercises.solutions

import m01_classes.exercises.M01Factory
import m01_classes.exercises.ProductSpec
import m01_classes.exercises.runChecks

private var slugRuns = 0

class SolutionProduct(
    override val id: Int,
    override val name: String,
    override val priceCents: Long,
) : ProductSpec {

    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(priceCents >= 0) { "price must not be negative" }
    }

    // Secondary constructor: must delegate to the primary one
    constructor(name: String, priceCents: Long) : this(-1, name, priceCents)

    // Computed on every access - no backing field
    override val priceLabel: String
        get() = "EGP %d.%02d".format(priceCents / 100, priceCents % 100)

    // `field` is the hidden backing field; the setter guards it
    override var stock: Int = 0
        set(value) {
            if (value >= 0) field = value
        }

    override val slug: String by lazy {
        slugRuns++
        name.lowercase().replace(' ', '-')
    }
}

object FactorySolution : M01Factory {
    override fun product(id: Int, name: String, priceCents: Long): ProductSpec = SolutionProduct(id, name, priceCents)
    override fun draft(name: String, priceCents: Long): ProductSpec = SolutionProduct(name, priceCents)
    override fun slugComputations(): Int = slugRuns
}

fun main() = runChecks(FactorySolution)
