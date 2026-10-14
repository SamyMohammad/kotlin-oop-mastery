package m18_patterns

import common.log
import common.note
import common.section

/*
 * L01 - Strategy and Factory
 *
 * STRATEGY: a family of interchangeable algorithms behind one interface;
 *           the caller picks one at runtime. In Kotlin often just a fun interface or lambda.
 * FACTORY:  one place that decides WHICH object to create from some input,
 *           so callers never write `when (type) { ... -> SomeClass() }` themselves.
 */

fun interface PaymentStrategy {
    fun pay(amountCents: Long): String
}

class CardPayment(private val last4: String) : PaymentStrategy {
    override fun pay(amountCents: Long) = "charged $amountCents to card *$last4 (+2% fee: ${amountCents * 2 / 100})"
}

class WalletPayment(private val phone: String) : PaymentStrategy {
    override fun pay(amountCents: Long) = "debited $amountCents from wallet $phone"
}

val cashOnDelivery = PaymentStrategy { "collect $it in cash on delivery" }

sealed interface PaymentChoice {
    data class Ok(val strategy: PaymentStrategy) : PaymentChoice
    data class Unknown(val method: String) : PaymentChoice
}

object PaymentFactory {
    fun from(method: String, details: String = ""): PaymentChoice = when (method) {
        "card" -> PaymentChoice.Ok(CardPayment(details.takeLast(4)))
        "wallet" -> PaymentChoice.Ok(WalletPayment(details))
        "cash" -> PaymentChoice.Ok(cashOnDelivery)
        else -> PaymentChoice.Unknown(method)
    }
}

class Checkout(private val strategy: PaymentStrategy) {
    fun complete(totalCents: Long) = strategy.pay(totalCents)
}

fun main() {
    section("1. Strategy: same checkout, different algorithms")
    for (s in listOf(CardPayment("4242"), WalletPayment("010-111"), cashOnDelivery)) {
        log(Checkout(s).complete(10_000))
    }

    section("2. Factory: choose from user input")
    val inputs = listOf("card" to "4111111111111111", "cash" to "", "bitcoin" to "")
    for ((method, details) in inputs) {
        when (val choice = PaymentFactory.from(method, details)) {
            is PaymentChoice.Ok -> log(Checkout(choice.strategy).complete(5_000))
            is PaymentChoice.Unknown -> log("unsupported payment method: ${choice.method}")
        }
    }
    note("The factory returns a sealed result instead of throwing on unknown input.")
}
