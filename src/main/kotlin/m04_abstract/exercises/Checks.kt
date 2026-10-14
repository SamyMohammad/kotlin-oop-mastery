package m04_abstract.exercises

import common.check
import common.runCheckSuite

/** Template: the learner must NOT change this class. */
abstract class Report(private val title: String) {
    protected abstract fun rows(): List<String>
    protected open fun footer(): String = "end"
    fun render(): String = buildString {
        appendLine("# $title")
        rows().forEach { appendLine("- $it") }
        append(footer())
    }
}

interface M04Factory {
    /** rows: one per order "order <id>" in given order; footer: "total: <n> orders" */
    fun ordersReport(orderIds: List<Int>): Report
    /** rows: "<name>: <qty>" only for qty < 5 (low stock); default footer. */
    fun lowStockReport(stock: Map<String, Int>): Report
}

fun runChecks(f: M04Factory) = runCheckSuite("m04 checks") {
    check("orders report renders") {
        f.ordersReport(listOf(7, 9)).render() == "# Orders\n- order 7\n- order 9\ntotal: 2 orders"
    }
    check("orders report empty") { f.ordersReport(emptyList()).render() == "# Orders\ntotal: 0 orders" }
    check("low stock filters") {
        f.lowStockReport(linkedMapOf("Pen" to 2, "Mug" to 10, "Cap" to 4)).render() ==
            "# Low stock\n- Pen: 2\n- Cap: 4\nend"
    }
}
