package m13_generics

import common.log
import common.note
import common.section

/*
 * L04 - Type erasure, reified, star projection
 *
 * At runtime the JVM erases type arguments: List<String> and List<Int> are both just List.
 *   if (x is List<String>)   <- error: Cannot check for instance of erased type 'List<String>'
 *
 * inline + reified keeps T available inside the function, because the body is
 * copied into each call site with the real type plugged in.
 *
 * Star projection List<*>: "a list of SOMETHING, I don't know what".
 * You can read elements as Any?, but you cannot add to it.
 */

inline fun <reified T> List<Any>.filterIsType(): List<T> = filter { it is T }.map { it as T }

inline fun <reified T> typeName(): String = T::class.simpleName ?: "?"

fun describe(list: List<*>): String {
    val first: Any? = list.firstOrNull() // reading is fine, as Any?
    return "size=${list.size}, first=$first"
}

fun main() {
    section("1. reified filter")
    val mixed: List<Any> = listOf("Pen", 500, "Mug", 9.99, 42)
    log("strings = ${mixed.filterIsType<String>()}")
    log("ints    = ${mixed.filterIsType<Int>()}")

    section("2. The type itself is available")
    log(typeName<String>())
    log(typeName<Book>())

    section("3. Star projection")
    log(describe(listOf("a", "b")))
    log(describe(listOf(1, 2, 3)))
    note("List<*> accepts any list; use it when you don't care about the element type.")

    section("4. The stdlib already has it")
    log(mixed.filterIsInstance<Double>())
}
