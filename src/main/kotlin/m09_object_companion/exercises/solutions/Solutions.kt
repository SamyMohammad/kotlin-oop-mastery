package m09_object_companion.exercises.solutions

import m09_object_companion.exercises.CouponLike
import m09_object_companion.exercises.CouponParser
import m09_object_companion.exercises.M09Factory
import m09_object_companion.exercises.runChecks

/** One instance for the whole program, created on first access. */
object ParseCounter {
    var calls = 0
        private set

    fun record() {
        calls++
    }
}

class Coupon private constructor(
    override val code: String,
    override val percent: Int,
) : CouponLike {

    // Unnamed companion = Coupon.Companion. It is a real object, so it can implement an interface.
    companion object : CouponParser {
        private val FORMAT = Regex("""([A-Z]{4,})(\d{1,2})""")

        override fun parse(raw: String): CouponLike? {
            ParseCounter.record()
            val match = FORMAT.matchEntire(raw) ?: return null
            val percent = match.groupValues[2].toInt()
            if (percent !in 1..90) return null
            return Coupon(raw, percent) // the private constructor is reachable from the companion
        }
    }
}

object FactorySolution : M09Factory {
    override fun parser(): CouponParser = Coupon // the class name used as a value = its companion
    override fun counter(): Any = ParseCounter
    override fun parseCalls(): Int = ParseCounter.calls
}

fun main() = runChecks(FactorySolution)
