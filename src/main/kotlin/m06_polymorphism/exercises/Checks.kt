package m06_polymorphism.exercises

import common.check
import common.runCheckSuite

interface Shape2D { fun area(): Double; fun name(): String }

interface M06Factory {
    fun circle(r: Double): Shape2D       // name "circle", area PI*r*r
    fun rect(w: Double, h: Double): Shape2D // name "rect"
    fun square(side: Double): Shape2D    // name "square", MUST be a subclass of the rect class
    /** Sum of areas, polymorphically (no type checks). */
    fun totalArea(shapes: List<Shape2D>): Double
    /** "circle r=<r>" for circles, "rect <w>x<h>" for rects AND squares, using smart casts on your own classes.
     *  Numbers printed with Kotlin's default Double.toString(). Anything else -> "unknown". */
    fun describe(shape: Any): String
}

fun runChecks(f: M06Factory) = runCheckSuite("m06 checks") {
    fun close(a: Double, b: Double) = kotlin.math.abs(a - b) < 1e-9
    check("circle area") { close(f.circle(1.0).area(), Math.PI) }
    check("square is a rect") { f.rect(1.0, 1.0)::class.isInstance(f.square(2.0)) }
    check("square name overrides") { f.square(2.0).name() == "square" }
    check("total area") { close(f.totalArea(listOf(f.rect(2.0, 3.0), f.square(2.0))), 10.0) }
    check("describe circle") { f.describe(f.circle(1.5)) == "circle r=1.5" }
    check("describe square as rect") { f.describe(f.square(2.0)) == "rect 2.0x2.0" }
    check("describe unknown") { f.describe("hello") == "unknown" }
}
