package m04_abstract.exercises.solutions

import m04_abstract.exercises.M04Factory
import m04_abstract.exercises.Report
import m04_abstract.exercises.runChecks

class OrdersReport(private val ids: List<Int>) : Report("Orders") {
    override fun rows(): List<String> = ids.map { "order $it" }
    override fun footer(): String = "total: ${ids.size} orders"
}

class LowStockReport(private val stock: Map<String, Int>) : Report("Low stock") {
    // Only the required step is overridden; footer() keeps the default "end"
    override fun rows(): List<String> =
        stock.filter { (_, qty) -> qty < 5 }.map { (name, qty) -> "$name: $qty" }
}

object FactorySolution : M04Factory {
    override fun ordersReport(orderIds: List<Int>): Report = OrdersReport(orderIds)
    override fun lowStockReport(stock: Map<String, Int>): Report = LowStockReport(stock)
}

fun main() = runChecks(FactorySolution)
