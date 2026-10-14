package m14_extensions_receivers

import common.log
import common.note
import common.section

/*
 * L03 - Lambdas with receiver
 *
 *  normal lambda:        (Receipt) -> Unit      inside: it.addLine(...)
 *  lambda with receiver: Receipt.() -> Unit     inside: addLine(...)   (this = Receipt)
 *
 * This is how buildString, apply, with and every Kotlin DSL work.
 *
 * Scope functions:
 *   let   it    returns lambda result
 *   run   this  returns lambda result
 *   with  this  returns lambda result (not an extension)
 *   apply this  returns the object itself
 *   also  it    returns the object itself
 */

class Receipt {
    private val lines = mutableListOf<String>()
    fun addLine(text: String) {
        lines += text
    }
    fun render() = lines.joinToString(" | ")
}

fun receipt(block: Receipt.() -> Unit): String {
    val r = Receipt()
    r.block() // call the lambda WITH r as its receiver
    return r.render()
}

fun main() {
    section("1. Our own receiver lambda")
    val text = receipt {
        addLine("Pen x2")      // this.addLine
        addLine("Mug x1")
        addLine("Total 40.00")
    }
    log(text)

    section("2. buildString is the same idea")
    val s = buildString {
        append("Order #")
        append(1042)
    }
    log(s)

    section("3. Scope functions")
    val cart = mutableListOf("Pen")
    val sizeAfter = cart.run { add("Mug"); size }   // run: this, returns result
    val same = cart.apply { add("Cap") }            // apply: this, returns cart
    val upper = cart.let { list -> list.map { it.uppercase() } } // let: it, returns result
    cart.also { log("also sees ${it.size} items") }  // also: it, returns cart
    log("sizeAfter=$sizeAfter, apply returned same list: ${if (same === cart) "same" else "different"}")
    log("upper=$upper")
    note("apply/also return the object - good for configuring. let/run return the result.")
}
