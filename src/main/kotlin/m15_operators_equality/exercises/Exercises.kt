package m15_operators_equality.exercises

/*
 * Module 15 exercises. Write a REGULAR class (not data):
 *
 *   class MyVec(override val x: Int, override val y: Int) : Vec, Comparable<MyVec> {
 *       operator fun plus(o: MyVec) = ...
 *       operator fun times(k: Int) = ...
 *       operator fun unaryMinus() = ...
 *       override fun compareTo(other: MyVec): Int = ...     // by x*x + y*y
 *       override fun equals(other: Any?): Boolean = ...
 *       override fun hashCode(): Int = ...
 *       override fun toString() = "Vec($x, $y)"
 *   }
 * Then use the operators (a + b, a * k, -a, a.compareTo(b)) in the functions below.
 */

object MyOps : M15Ops {
    override fun vec(x: Int, y: Int): Vec = TODO()
    override fun add(a: Vec, b: Vec): Vec = TODO()
    override fun scale(a: Vec, k: Int): Vec = TODO()
    override fun negate(a: Vec): Vec = TODO()

    /** Return the SIGN only: -1, 0 or 1 (Int.sign helps). */
    override fun compare(a: Vec, b: Vec): Int = TODO()

    override fun show(a: Vec): String = TODO()
}

fun main() = runChecks(MyOps)
