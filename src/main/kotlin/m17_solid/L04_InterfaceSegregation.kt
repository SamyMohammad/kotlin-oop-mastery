package m17_solid

import common.log
import common.note
import common.section

/*
 * L04 - I: Interface Segregation Principle
 *
 * "No client should be forced to depend on methods it does not use."
 *
 * BEFORE: one fat PaymentGateway. Cash on delivery cannot refund online or
 * subscribe, so it throws from half its methods.
 * AFTER:  small role interfaces; each class implements only what it can do.
 */

// ---------- BEFORE ----------
interface PaymentGateway {
    fun pay(cents: Long): String
    fun refund(cents: Long): String
    fun subscribe(planId: String): String
}

class CashGatewayBad : PaymentGateway {
    override fun pay(cents: Long) = "cash collected: $cents"
    override fun refund(cents: Long): String = throw UnsupportedOperationException("cash cannot refund online")
    override fun subscribe(planId: String): String = throw UnsupportedOperationException("cash cannot subscribe")
}

// ---------- AFTER ----------
interface Payer {
    fun pay(cents: Long): String
}

interface Refunder {
    fun refund(cents: Long): String
}

interface Subscriber {
    fun subscribe(planId: String): String
}

class CardGateway : Payer, Refunder, Subscriber {
    override fun pay(cents: Long) = "card charged: $cents"
    override fun refund(cents: Long) = "card refunded: $cents"
    override fun subscribe(planId: String) = "card subscribed to $planId"
}

class CashGateway : Payer {
    override fun pay(cents: Long) = "cash collected: $cents"
}

// Clients ask only for the role they need
fun checkout(payer: Payer, cents: Long) = payer.pay(cents)
fun returnItem(refunder: Refunder, cents: Long) = refunder.refund(cents)

fun main() {
    section("BEFORE: a fat interface")
    val bad: PaymentGateway = CashGatewayBad()
    log(bad.pay(5_000))
    try {
        bad.refund(5_000)
    } catch (e: UnsupportedOperationException) {
        log("refund blew up at runtime: ${e.message}")
    }

    section("AFTER: role interfaces")
    log(checkout(CashGateway(), 5_000))
    log(checkout(CardGateway(), 5_000))
    log(returnItem(CardGateway(), 5_000))
    // returnItem(CashGateway(), 5_000)  <- does not compile: CashGateway is not a Refunder
    note("The impossible call moved from a runtime crash to a compile error.")
}
