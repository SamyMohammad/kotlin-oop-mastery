package m06_polymorphism.exercises

/*
 * Module 06 exercises. Implement Shape2D (Checks.kt) with your own classes:
 *   class Circle(val r: Double) : Shape2D
 *   open class Rect(val w: Double, val h: Double) : Shape2D   (open!)
 *   class Square(side: Double) : Rect(side, side)               (override name())
 */

object MyFactory : M06Factory {
    override fun circle(r: Double): Shape2D = TODO()
    override fun rect(w: Double, h: Double): Shape2D = TODO()

    /** Must extend your Rect class. */
    override fun square(side: Double): Shape2D = TODO()

    /** Just call area() on each - no `is` checks. Dynamic dispatch picks the right one. */
    override fun totalArea(shapes: List<Shape2D>): Double = TODO()

    /** Use `when (shape) { is Circle -> ...; is Rect -> ...; else -> "unknown" }` with smart casts. */
    override fun describe(shape: Any): String = TODO()
}

fun main() = runChecks(MyFactory)
