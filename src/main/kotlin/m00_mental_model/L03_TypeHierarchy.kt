package m00_mental_model

import common.log
import common.note
import common.section

/*
 * L03 - Kotlin's type hierarchy: Any, Any?, Unit, Nothing
 *
 *          Any?
 *         /    \
 *       Any    null
 *     /  |  \
 *   Int String Cart ...
 *     \  |  /
 *     Nothing      (subtype of EVERYTHING, has no values)
 */

fun hello() {
    // no return type written -> the function returns Unit
}

// Nothing = "this function never returns normally"
fun fail(message: String): Nothing = throw IllegalStateException(message)

@Suppress("USELESS_IS_CHECK") // always true - that is the lesson
fun describe(x: Any): String = "${x::class.simpleName} is Any? ${x is Any}"

fun main() {
    section("1. Everything is an Any")
    log(describe(42))
    log(describe("text"))
    log(describe(Cart()))
    note("Any is the root of every class, like Object in Java.")

    section("2. Any vs Any?")
    val maybe: Any? = null
    log("maybe is Any? ${maybe is Any}")
    log("maybe == null? ${maybe == null}")
    note("null is NOT an Any. It only fits in Any? (the nullable root).")

    section("3. Unit is a real object")
    val result = hello()
    log("hello() returned: $result")
    log("same object as Unit? ${if (result === Unit) "same" else "different"}")
    note("Unit is like void, but it is a value you can store and print.")

    section("4. Nothing - the function that never returns")
    val input: String? = "Pen"
    val name: String = input ?: fail("name missing")
    log("name = $name   (type is String, not String?)")
    try {
        val missing: String? = null
        val other: String = missing ?: fail("name missing")
        log("never printed $other")
    } catch (e: IllegalStateException) {
        log("thrown: ${e::class.simpleName}(${e.message})")
    }
    note("Because fail() returns Nothing, the compiler knows `name` is never null.")
}
