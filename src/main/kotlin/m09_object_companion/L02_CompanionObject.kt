package m09_object_companion

import common.log
import common.note
import common.section

/*
 * L02 - companion object
 *
 * Kotlin has no `static`. A companion object is an object tied to its class:
 *   - its members are called through the class name:  Order.nextId()
 *   - it can see the class's private members (and vice versa)
 *   - it is a real object: it can implement interfaces and be passed around
 *   - default name is Companion; you may give it another name
 *   - @JvmStatic makes a real static method for Java callers
 */

interface JsonParser<T> {
    fun fromJson(json: String): T
}

class Order private constructor(val id: Int, val total: Long) {
    override fun toString() = "Order(id=$id, total=$total)"

    companion object : JsonParser<Order> {
        private var lastId = 0

        fun create(total: Long) = Order(++lastId, total)

        // super-tiny parser for strings like "id=5;total=900"
        override fun fromJson(json: String): Order {
            val parts = json.split(";").associate { it.substringBefore("=") to it.substringAfter("=") }
            return Order(parts.getValue("id").toInt(), parts.getValue("total").toLong())
        }
    }
}

class Invoice {
    companion object Numbering { // named companion
        fun next() = "INV-001"
    }
}

fun <T> load(parser: JsonParser<T>, json: String): T = parser.fromJson(json)

fun main() {
    section("1. Called through the class name")
    log(Order.create(500))
    log(Order.create(1200))
    // Order(9, 1)  <- does not compile: constructor is private

    section("2. The companion is an object you can pass")
    log(load(Order, "id=42;total=900"))
    note("`Order` used as a value means Order.Companion, which implements JsonParser<Order>.")

    section("3. Named companion")
    log(Invoice.next())
    log(Invoice.Numbering.next())
}
