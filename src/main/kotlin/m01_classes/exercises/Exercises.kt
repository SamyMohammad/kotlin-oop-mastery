package m01_classes.exercises

/*
 * Module 01 exercises. Build a real class: constructors, init validation,
 * custom getter, custom setter with `field`, and a lazy property.
 *
 * Tip: write `class MyProduct(...) : ProductSpec { ... }` below and return it from the factory.
 */

/** Count slug computations here (increment inside your lazy block). */
var mySlugComputations = 0

object MyFactory : M01Factory {
    /** Primary constructor. Validate in init: blank name or negative price -> IllegalArgumentException (use require). */
    override fun product(id: Int, name: String, priceCents: Long): ProductSpec = TODO()

    /** Secondary constructor (name, priceCents) that calls this(-1, name, priceCents). */
    override fun draft(name: String, priceCents: Long): ProductSpec = TODO()

    override fun slugComputations(): Int = mySlugComputations
}

fun main() = runChecks(MyFactory)
