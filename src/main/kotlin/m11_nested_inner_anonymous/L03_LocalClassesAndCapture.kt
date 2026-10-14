package m11_nested_inner_anonymous

import common.log
import common.note
import common.section

/*
 * L03 - Local classes, local functions and capture
 *
 * Classes and functions can be declared INSIDE a function.
 * They (and lambdas and object expressions) can CAPTURE local variables -
 * even `var`s, and they can change them. Unlike Java, no "effectively final" rule.
 */

fun checkoutReport(prices: List<Long>): List<String> {
    var runningTotal = 0L
    val lines = mutableListOf<String>()

    // Local class: only exists inside this function
    class Line(val index: Int, val cents: Long) {
        fun render(): String {
            runningTotal += cents // captured var, modified
            return "#$index: $cents (running $runningTotal)"
        }
    }

    // Local function, also capturing
    fun addLine(index: Int, cents: Long) {
        lines += Line(index, cents).render()
    }

    prices.forEachIndexed { i, p -> addLine(i + 1, p) }
    lines += "TOTAL: $runningTotal"
    return lines
}

fun makeCounter(): () -> Int {
    var count = 0
    return { ++count } // the lambda keeps `count` alive after makeCounter returns
}

fun main() {
    section("1. Local class + local function")
    for (line in checkoutReport(listOf(500, 4500, 1000))) log(line)

    section("2. A lambda keeps captured state alive")
    val next = makeCounter()
    log(next())
    log(next())
    log(next())
    val other = makeCounter()
    log("a new counter starts again: ${other()}")
    note("Each call to makeCounter creates its own `count` box.")
}
