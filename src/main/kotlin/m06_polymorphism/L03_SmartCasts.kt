package m06_polymorphism

import common.log
import common.note
import common.section

/*
 * L03 - Type checks and smart casts
 *
 *  is / !is   check the runtime type
 *  as         unsafe cast: throws ClassCastException when wrong
 *  as?        safe cast: null when wrong
 *  smart cast after a successful `is`, the compiler treats the value as the narrower type
 *
 * Smart casts need a value that cannot change between check and use:
 * local val, val property in the same module without custom getter, ...
 * NOT a `var` property (another thread or call could change it).
 */

sealed interface CartEvent
class ItemAdded(val name: String, val qty: Int) : CartEvent
class ItemRemoved(val name: String) : CartEvent
class CouponApplied(val code: String) : CartEvent

fun render(event: CartEvent): String = when (event) {
    is ItemAdded -> "+${event.qty} ${event.name}"      // smart cast to ItemAdded
    is ItemRemoved -> "-${event.name}"
    is CouponApplied -> "coupon ${event.code}"
}

class Holder(var payload: Any?)

fun main() {
    section("1. Smart cast in when")
    val events = listOf(ItemAdded("Pen", 2), CouponApplied("SAVE10"), ItemRemoved("Mug"))
    for (e in events) log(render(e))

    section("2. as vs as?")
    val anything: Any = "not a number"
    log("as? Int -> ${anything as? Int}")
    try {
        anything as Int
    } catch (e: ClassCastException) {
        log("as Int  -> threw ${e::class.simpleName}")
    }

    section("3. Smart cast and var properties")
    val holder = Holder("Kotlin")
    // if (holder.payload is String) holder.payload.length
    //   <- error: Smart cast to 'String' is impossible, because 'payload' is a mutable property
    val local = holder.payload            // copy into a local val first
    if (local is String) log("length = ${local.length}")
    note("Copy a var property into a local val, then check it.")
}
