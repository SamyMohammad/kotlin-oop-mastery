package m18_patterns

import common.log
import common.note
import common.section

/*
 * L02 - Builder, and why Kotlin rarely needs it
 *
 * Java builders exist because Java has no named or default arguments.
 * Kotlin has both, so a data class constructor already IS a readable builder.
 * Builders still shine for step-by-step construction -> as a DSL (m14).
 */

// 1) Classic Java-style builder
class OrderJava private constructor(val id: Int, val items: List<String>, val giftWrap: Boolean, val note: String?) {
    class Builder(private val id: Int) {
        private val items = mutableListOf<String>()
        private var giftWrap = false
        private var note: String? = null
        fun addItem(name: String) = apply { items += name }
        fun giftWrap(value: Boolean) = apply { giftWrap = value }
        fun note(value: String) = apply { note = value }
        fun build() = OrderJava(id, items.toList(), giftWrap, note)
    }

    override fun toString() = "OrderJava(id=$id, items=$items, giftWrap=$giftWrap, note=$note)"
}

// 2) Idiomatic Kotlin: named + default arguments
data class Order(
    val id: Int,
    val items: List<String>,
    val giftWrap: Boolean = false,
    val note: String? = null,
)

// 3) DSL builder for step-by-step building
class OrderDslBuilder(private val id: Int) {
    private val items = mutableListOf<String>()
    var giftWrap = false
    var note: String? = null
    fun item(name: String, times: Int = 1) = repeat(times) { items += name }
    fun build() = Order(id, items.toList(), giftWrap, note)
}

fun order(id: Int, block: OrderDslBuilder.() -> Unit) = OrderDslBuilder(id).apply(block).build()

fun main() {
    section("1. Classic builder")
    val a = OrderJava.Builder(1).addItem("Pen").addItem("Mug").giftWrap(true).build()
    log(a)

    section("2. Named and default arguments")
    val b = Order(id = 2, items = listOf("Pen", "Mug"), giftWrap = true)
    log(b)
    log(b.copy(id = 3, note = "leave at door"))
    note("Same readability, no builder class, plus copy() for variations.")

    section("3. DSL when construction has steps or loops")
    val c = order(4) {
        item("Pen", times = 3)
        item("Mug")
        giftWrap = true
    }
    log(c)
}
