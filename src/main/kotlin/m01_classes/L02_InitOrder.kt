package m01_classes

import common.log
import common.note
import common.section

/*
 * L02 - Initialization order
 *
 * Property initializers and init blocks run TOP TO BOTTOM, in the order
 * they are written in the class body, right after the primary constructor.
 * Secondary constructor bodies run LAST.
 */

class Order(val id: Int) {
    val first = step("1. property `first` initializer")

    init {
        step("2. first init block")
        // `label` is declared below. Reading it directly does not compile,
        // but reading it through a function does - and gives null.
        log("   label inside first init = ${peekLabel()}")
    }

    val second = step("3. property `second` initializer")

    var label: String? = "order-$id"

    init {
        step("4. second init block, label = $label")
    }

    constructor(id: Int, note: String) : this(id) {
        step("5. secondary constructor body ($note)")
    }

    private fun peekLabel(): String? = label

    private fun step(text: String): String {
        log(text)
        return text
    }
}

fun main() {
    section("1. Primary constructor only")
    Order(7)
    note("Initializers and init blocks are one sequence, top to bottom.")

    section("2. Through the secondary constructor")
    Order(8, "gift")
    note("The secondary body runs after the whole primary initialization.")

    section("3. The trap")
    log("The compiler blocks direct reads of a property declared below an init block,")
    log("but a function call slips through and sees the default (null/0/false).")
    note("Keep init blocks after the properties they read.")
}
