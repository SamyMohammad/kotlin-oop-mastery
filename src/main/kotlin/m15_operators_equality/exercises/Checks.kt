package m15_operators_equality.exercises

import common.check
import common.runCheckSuite

/** Implement these operators on your own Vec class (NOT a data class - write equals/hashCode yourself). */
interface Vec { val x: Int; val y: Int }

interface M15Ops {
    fun vec(x: Int, y: Int): Vec
    /** a + b using your operator */
    fun add(a: Vec, b: Vec): Vec
    /** a * k using your operator */
    fun scale(a: Vec, k: Int): Vec
    /** -a using your unaryMinus */
    fun negate(a: Vec): Vec
    /** compare by squared length using your compareTo; returns sign -1/0/1 */
    fun compare(a: Vec, b: Vec): Int
    /** your Vec's toString: "Vec(1, 2)" */
    fun show(a: Vec): String
}

fun runChecks(ops: M15Ops) = runCheckSuite("m15 checks") {
    check("not a data class") { ops.vec(0, 0)::class.java.methods.none { it.name == "copy" || it.name == "component1" } }
    check("equals by value") { ops.vec(1, 2) == ops.vec(1, 2) && ops.vec(1, 2) != ops.vec(2, 1) }
    check("hashCode consistent") { setOf(ops.vec(1, 2), ops.vec(1, 2)).size == 1 }
    check("identity differs") { ops.vec(1, 2) !== ops.vec(1, 2) }
    check("plus") { ops.add(ops.vec(1, 2), ops.vec(3, 4)) == ops.vec(4, 6) }
    check("times") { ops.scale(ops.vec(1, -2), 3) == ops.vec(3, -6) }
    check("unaryMinus") { ops.negate(ops.vec(1, -2)) == ops.vec(-1, 2) }
    check("compareTo") { ops.compare(ops.vec(3, 4), ops.vec(5, 0)) == 0 && ops.compare(ops.vec(1, 1), ops.vec(2, 2)) == -1 }
    check("toString") { ops.show(ops.vec(1, 2)) == "Vec(1, 2)" }
}
