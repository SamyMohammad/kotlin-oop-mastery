package m10_value_classes.exercises

import common.check
import common.runCheckSuite

interface MoneyOps<M> {
    /** Create money from cents; negative -> IllegalArgumentException. */
    fun of(cents: Long): M
    fun plus(a: M, b: M): M
    /** Apply percent discount, rounding down to whole cents. percent outside 0..100 -> IllegalArgumentException */
    fun discount(m: M, percent: Int): M
    /** "EGP 12.50" */
    fun format(m: M): String
    /** Return M's class, e.g. Money::class. The suite verifies it is a value class. */
    fun type(): kotlin.reflect.KClass<*>
}

fun <M> runChecks(ops: MoneyOps<M>) = runCheckSuite("m10 checks") {
    // Value classes compile a static "box-impl" method; KClass.isValue would need kotlin-reflect.
    check("is a value class") { ops.type().java.declaredMethods.any { it.name == "box-impl" } }
    check("negative rejected") { runCatching { ops.of(-1) }.exceptionOrNull() is IllegalArgumentException }
    check("plus") { ops.format(ops.plus(ops.of(150), ops.of(250))) == "EGP 4.00" }
    check("discount rounds down") { ops.format(ops.discount(ops.of(999), 10)) == "EGP 8.99" }
    check("bad percent") { runCatching { ops.discount(ops.of(1), 101) }.exceptionOrNull() is IllegalArgumentException }
    check("equality by value") { ops.of(5) == ops.of(5) }
}
