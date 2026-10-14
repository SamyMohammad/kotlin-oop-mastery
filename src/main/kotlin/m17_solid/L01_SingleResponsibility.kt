package m17_solid

import common.log
import common.note
import common.section

/*
 * L01 - S: Single Responsibility Principle
 *
 * "A class should have one reason to change."
 * Not "one method" - one ACTOR / concern that can ask for changes.
 *
 * BEFORE: OrderManager changes when validation rules, pricing, storage OR emails change.
 * AFTER:  four focused classes, one small coordinator.
 */

// ---------- BEFORE ----------
class OrderManager {
    val savedRows = mutableListOf<String>()
    val sentEmails = mutableListOf<String>()

    fun placeOrder(id: Int, prices: List<Long>, email: String): Long {
        if (prices.isEmpty() || !email.contains("@")) throw IllegalArgumentException("invalid order") // validation
        val total = prices.sum() + (if (prices.sum() < 50_000) 2_500 else 0)                       // pricing
        savedRows += "INSERT $id $total"                                                         // storage
        sentEmails += "To $email: order $id = $total"                                            // messaging
        return total
    }
}

// ---------- AFTER ----------
class OrderValidator {
    fun validate(prices: List<Long>, email: String) = require(prices.isNotEmpty() && "@" in email) { "invalid order" }
}

class PriceCalculator {
    fun total(prices: List<Long>): Long = prices.sum() + shipping(prices.sum())
    private fun shipping(subtotal: Long) = if (subtotal < 50_000) 2_500L else 0L
}

class OrderRepository {
    val rows = mutableListOf<String>()
    fun save(id: Int, total: Long) {
        rows += "INSERT $id $total"
    }
}

class ReceiptSender {
    val sent = mutableListOf<String>()
    fun send(email: String, id: Int, total: Long) {
        sent += "To $email: order $id = $total"
    }
}

class PlaceOrder(
    private val validator: OrderValidator,
    private val calculator: PriceCalculator,
    private val repository: OrderRepository,
    private val sender: ReceiptSender,
) {
    fun execute(id: Int, prices: List<Long>, email: String): Long {
        validator.validate(prices, email)
        val total = calculator.total(prices)
        repository.save(id, total)
        sender.send(email, id, total)
        return total
    }
}

fun main() {
    section("BEFORE: one class, four reasons to change")
    val manager = OrderManager()
    log("total = ${manager.placeOrder(1, listOf(10_000, 20_000), "sara@mail.com")}")
    log(manager.savedRows.single())
    log(manager.sentEmails.single())

    section("AFTER: same behaviour, separated concerns")
    val repo = OrderRepository()
    val sender = ReceiptSender()
    val placeOrder = PlaceOrder(OrderValidator(), PriceCalculator(), repo, sender)
    log("total = ${placeOrder.execute(1, listOf(10_000, 20_000), "sara@mail.com")}")
    log(repo.rows.single())
    log(sender.sent.single())

    section("What changed for testing")
    log("PriceCalculator can be tested alone: ${PriceCalculator().total(listOf(60_000))}")
    note("A shipping rule change now touches PriceCalculator only - nothing else recompiles in your head.")
}
