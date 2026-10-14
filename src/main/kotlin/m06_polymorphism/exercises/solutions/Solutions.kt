package m06_polymorphism.exercises.solutions

import m06_polymorphism.exercises.M06Factory
import m06_polymorphism.exercises.Shape2D
import m06_polymorphism.exercises.runChecks

class Circle(val r: Double) : Shape2D {
    override fun area() = Math.PI * r * r
    override fun name() = "circle"
}

open class Rect(val w: Double, val h: Double) : Shape2D {
    override fun area() = w * h
    override fun name() = "rect"
}

class Square(side: Double) : Rect(side, side) {
    override fun name() = "square"
}

object FactorySolution : M06Factory {
    override fun circle(r: Double): Shape2D = Circle(r)
    override fun rect(w: Double, h: Double): Shape2D = Rect(w, h)
    override fun square(side: Double): Shape2D = Square(side)

    // Dynamic dispatch: each shape's own area() runs
    override fun totalArea(shapes: List<Shape2D>): Double = shapes.sumOf { it.area() }

    // Smart casts: inside each branch `shape` already has the narrower type
    override fun describe(shape: Any): String = when (shape) {
        is Circle -> "circle r=${shape.r}"
        is Rect -> "rect ${shape.w}x${shape.h}" // Square lands here too: it IS a Rect
        else -> "unknown"
    }
}

fun main() = runChecks(FactorySolution)
