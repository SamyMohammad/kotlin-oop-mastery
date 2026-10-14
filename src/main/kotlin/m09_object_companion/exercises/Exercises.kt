package m09_object_companion.exercises

/*
 * Module 09 exercises.
 *
 * Tip:
 *   object ParseCounter { var calls = 0 }          // singleton
 *
 *   class Coupon private constructor(override val code: String, override val percent: Int) : CouponLike {
 *       companion object : CouponParser {             // unnamed companion implementing an interface
 *           override fun parse(raw: String): CouponLike? { ParseCounter.calls++; ... }
 *       }
 *   }
 * A Regex like Regex("""([A-Z]{4,})(\d{1,2})""") with matchEntire helps.
 */

object MyFactory : M09Factory {
    /** Return Coupon (the companion object itself - `Coupon` used as a value means Coupon.Companion). */
    override fun parser(): CouponParser = TODO()

    /** Return your counting `object`. */
    override fun counter(): Any = TODO()

    override fun parseCalls(): Int = TODO()
}

fun main() = runChecks(MyFactory)
