package m04_abstract

import common.log
import common.note
import common.section

/*
 * L02 - Template method pattern
 *
 * The base class owns the ALGORITHM (a final function with fixed steps).
 * Subclasses own the DETAILS (abstract or open steps).
 */

abstract class CheckoutStep(private val name: String) {

    // The template: not open, so the order of steps can never change
    fun run(): Boolean {
        log("[$name] start")
        if (!validate()) {
            log("[$name] validation failed, stopping")
            return false
        }
        execute()
        afterExecute()
        log("[$name] done")
        return true
    }

    protected abstract fun validate(): Boolean   // required step
    protected abstract fun execute()             // required step
    protected open fun afterExecute() {}         // optional hook with a default
}

class ReserveStock(private val qty: Int, private val available: Int) : CheckoutStep("reserve") {
    override fun validate() = qty <= available
    override fun execute() = log("   reserving $qty item(s)")
}

class ChargeCard(private val amountCents: Long) : CheckoutStep("charge") {
    override fun validate() = amountCents > 0
    override fun execute() = log("   charging $amountCents cents")
    override fun afterExecute() = log("   sending receipt email")
}

fun main() {
    section("1. Same algorithm, different details")
    ReserveStock(qty = 2, available = 10).run()
    ChargeCard(amountCents = 15_000).run()

    section("2. A step that fails validation")
    ReserveStock(qty = 20, available = 3).run()
    note("The base guarantees: no execute() without a passing validate().")
}
