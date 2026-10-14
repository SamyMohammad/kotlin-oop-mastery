package m08_sealed_enums

import common.log
import common.note
import common.section
import m08_sealed_enums.shop.OrderStatus
import m08_sealed_enums.shop.label

/*
 * L04 - Exhaustive when
 *
 * When `when` is used as an EXPRESSION on a sealed type or enum, it must
 * cover every case. With all cases covered, no `else` is needed.
 *
 * Add a new subtype (e.g. Returned) and every exhaustive `when` stops compiling:
 *   "'when' expression must be exhaustive. Add the 'is Returned' branch or an 'else' branch."
 * That compile error is a FEATURE: the compiler lists every place you must update.
 *
 * An `else ->` branch silences that help forever. Avoid it on sealed types.
 */

// State machine: which transitions are allowed
fun canCancel(status: OrderStatus): Boolean = when (status) {
    OrderStatus.Placed -> true
    is OrderStatus.Shipped -> false
    is OrderStatus.Delivered -> false
    is OrderStatus.Cancelled -> false
}

// The risky version: compiles today and will silently say "false" for any future status
fun canCancelRisky(status: OrderStatus): Boolean = when (status) {
    OrderStatus.Placed -> true
    else -> false
}

fun main() {
    section("1. Labels for every status")
    val history = listOf(
        OrderStatus.Placed,
        OrderStatus.Shipped("EG123"),
        OrderStatus.Delivered("Sara"),
        OrderStatus.Cancelled("changed mind"),
    )
    for (s in history) log("${s.label()} | can cancel: ${canCancel(s)}")

    section("2. Why no else")
    log("canCancel      : every case listed, compiler re-checks on each new subtype")
    log("canCancelRisky : else hides new subtypes, today it returns ${canCancelRisky(OrderStatus.Shipped("X"))} for Shipped")
    note("Imagine adding OrderStatus.OnHold. Only canCancel would force you to decide.")
}
