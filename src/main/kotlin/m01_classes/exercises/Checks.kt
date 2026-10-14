package m01_classes.exercises

import common.check
import common.runCheckSuite

interface ProductSpec {
    val id: Int
    val name: String
    val priceCents: Long
    /** "EGP 12.50" for 1250 cents; always two decimals. */
    val priceLabel: String
    /** Setting a negative value is ignored (keeps the old value). Starts at 0. */
    var stock: Int
    /** name lowercased, spaces replaced by '-'. Computed once (lazy). */
    val slug: String
}

interface M01Factory {
    /** Throws IllegalArgumentException when priceCents < 0 or name is blank. */
    fun product(id: Int, name: String, priceCents: Long): ProductSpec
    /** Secondary-constructor style: id defaults to -1. */
    fun draft(name: String, priceCents: Long): ProductSpec
    /** How many times the slug computation ran in total, across all products created by this factory. */
    fun slugComputations(): Int
}

fun runChecks(f: M01Factory) = runCheckSuite("m01 checks") {
    check("price label formats cents") { f.product(1, "Pen", 1250).priceLabel == "EGP 12.50" }
    check("price label pads zero") { f.product(1, "Pen", 5).priceLabel == "EGP 0.05" }
    check("negative price rejected") {
        runCatching { f.product(1, "Pen", -1) }.exceptionOrNull() is IllegalArgumentException
    }
    check("blank name rejected") {
        runCatching { f.product(1, " ", 10) }.exceptionOrNull() is IllegalArgumentException
    }
    check("draft id is -1") { f.draft("Mug", 100).id == -1 }
    check("stock ignores negatives") {
        val p = f.product(1, "Pen", 10); p.stock = 5; p.stock = -3; p.stock == 5
    }
    check("slug format") { f.product(1, "Blue Pen XL", 10).slug == "blue-pen-xl" }
    check("slug computed once") {
        val before = f.slugComputations()
        val p = f.product(2, "Cup", 10); p.slug; p.slug; p.slug
        f.slugComputations() - before == 1
    }
}
