package m04_abstract.exercises

/*
 * Module 04 exercises. Report (in Checks.kt) is a template: render() is fixed,
 * you only fill in the abstract/open steps.
 *
 * Tip:
 *   class OrdersReport(private val ids: List<Int>) : Report("Orders") {
 *       override fun rows(): List<String> = ...
 *       override fun footer(): String = ...
 *   }
 */

object MyFactory : M04Factory {
    /** Title "Orders", one row per id ("order 7"), footer "total: <n> orders". */
    override fun ordersReport(orderIds: List<Int>): Report = TODO()

    /** Title "Low stock", rows "<name>: <qty>" for qty < 5 only, keep the default footer. */
    override fun lowStockReport(stock: Map<String, Int>): Report = TODO()
}

fun main() = runChecks(MyFactory)
