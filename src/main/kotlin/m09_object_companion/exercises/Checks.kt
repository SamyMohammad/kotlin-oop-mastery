package m09_object_companion.exercises

import common.check
import common.runCheckSuite

/** Contract for a class with a private constructor and companion factory. */
interface CouponLike { val code: String; val percent: Int }

interface CouponParser {
    /** "SAVE10" -> code "SAVE10", percent 10. Format: 4+ uppercase letters followed by 1-2 digits (1..90).
     *  Invalid -> null. */
    fun parse(raw: String): CouponLike?
}

interface M09Factory {
    /** Return your Coupon class's companion object (it must implement CouponParser). */
    fun parser(): CouponParser
    /** Return your singleton object that counts parse() calls across the whole program. Same instance every call. */
    fun counter(): Any
    /** Number of parse() calls recorded by the singleton so far. */
    fun parseCalls(): Int
}

fun runChecks(f: M09Factory) = runCheckSuite("m09 checks") {
    check("valid coupon") { f.parser().parse("SAVE10")?.let { it.code == "SAVE10" && it.percent == 10 } == true }
    check("lowercase invalid") { f.parser().parse("save10") == null }
    check("percent over 90 invalid") { f.parser().parse("SAVE95") == null }
    check("percent zero invalid") { f.parser().parse("SAVE0") == null }
    check("short prefix invalid") { f.parser().parse("AB10") == null }
    check("parser is a companion") { f.parser()::class.java.simpleName == "Companion" }
    check("singleton identity") { f.counter() === f.counter() }
    check("counter counts") {
        val before = f.parseCalls(); f.parser().parse("SAVE10"); f.parser().parse("x")
        f.parseCalls() - before == 2
    }
}
