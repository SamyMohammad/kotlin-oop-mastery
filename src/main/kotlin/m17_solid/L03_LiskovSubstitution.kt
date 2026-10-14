package m17_solid

import common.log
import common.note
import common.section

/*
 * L03 - L: Liskov Substitution Principle
 *
 * "Code that works with a base type must keep working with ANY subtype."
 * A subtype may not weaken what the base promises.
 *
 * Classic violation: Square extends Rectangle with independent setters.
 * The Rectangle promise "setting width does not change height" breaks.
 */

// ---------- BEFORE ----------
open class Rectangle(open var width: Int, open var height: Int) {
    fun area() = width * height
}

class SquareBad(side: Int) : Rectangle(side, side) {
    override var width: Int
        get() = super.width
        set(value) {
            super.width = value
            super.height = value
        }
    override var height: Int
        get() = super.height
        set(value) {
            super.width = value
            super.height = value
        }
}

// Written against Rectangle's promise
fun stretch(r: Rectangle): String {
    r.width = 5
    r.height = 4
    return "expected area 20, got ${r.area()}"
}

// ---------- AFTER ----------
sealed interface Shape {
    val area: Int
}

data class Rect(val width: Int, val height: Int) : Shape {
    override val area get() = width * height
}

data class Square(val side: Int) : Shape {
    override val area get() = side * side
}

fun main() {
    section("BEFORE: a subtype that breaks the promise")
    log("Rectangle: ${stretch(Rectangle(1, 1))}")
    log("SquareBad: ${stretch(SquareBad(1))}")
    note("Same function, a valid-looking subtype, a wrong answer.")

    section("AFTER: immutable shapes, no false promise")
    val shapes: List<Shape> = listOf(Rect(5, 4), Square(4))
    for (s in shapes) log("$s area=${s.area}")
    note("Square is no longer a Rectangle with setters. Every Shape honours what Shape promises.")
}
