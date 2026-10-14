package m15_operators_equality.exercises.solutions

import m15_operators_equality.exercises.M15Ops
import m15_operators_equality.exercises.Vec
import m15_operators_equality.exercises.runChecks
import kotlin.math.sign

class MyVec(override val x: Int, override val y: Int) : Vec, Comparable<MyVec> {
    operator fun plus(o: MyVec) = MyVec(x + o.x, y + o.y)
    operator fun times(k: Int) = MyVec(x * k, y * k)
    operator fun unaryMinus() = MyVec(-x, -y)

    private val lengthSquared get() = x * x + y * y
    override fun compareTo(other: MyVec): Int = lengthSquared.compareTo(other.lengthSquared)

    // equals and hashCode must agree: equal vectors -> equal hash codes
    override fun equals(other: Any?): Boolean = other is MyVec && other.x == x && other.y == y
    override fun hashCode(): Int = 31 * x + y
    override fun toString() = "Vec($x, $y)"
}

private fun Vec.my() = this as MyVec

object OpsSolution : M15Ops {
    override fun vec(x: Int, y: Int): Vec = MyVec(x, y)
    override fun add(a: Vec, b: Vec): Vec = a.my() + b.my()
    override fun scale(a: Vec, k: Int): Vec = a.my() * k
    override fun negate(a: Vec): Vec = -a.my()
    override fun compare(a: Vec, b: Vec): Int = a.my().compareTo(b.my()).sign
    override fun show(a: Vec): String = a.toString()
}

fun main() = runChecks(OpsSolution)
