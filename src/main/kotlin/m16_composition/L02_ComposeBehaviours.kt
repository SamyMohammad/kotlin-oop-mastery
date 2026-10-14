package m16_composition

import common.log
import common.note
import common.section

/*
 * L02 - Composition: has-a instead of is-a
 *
 * Each feature is ONE small class that wraps ANY Sender.
 * Combinations are built at runtime by nesting - no new classes needed.
 *   channels + features   instead of   channels x 2^features
 */

interface Sender {
    fun send(msg: String): String
}

class Sms : Sender {
    override fun send(msg: String) = "SMS: $msg"
}

class Email : Sender {
    override fun send(msg: String) = "EMAIL: $msg"
}

class Logging(private val inner: Sender) : Sender {          // HAS a Sender
    override fun send(msg: String) = "[log] " + inner.send(msg)
}

class Retrying(private val inner: Sender, private val times: Int) : Sender {
    override fun send(msg: String) = "[retry x$times] " + inner.send(msg)
}

class Uppercase(private val inner: Sender) : Sender {
    override fun send(msg: String) = inner.send(msg.uppercase())
}

// The order service only knows the Sender contract
class OrderService(private val sender: Sender) {
    fun ship(orderId: Int) = sender.send("order $orderId shipped")
}

fun main() {
    section("1. Build combinations at runtime")
    log(Logging(Sms()).send("hi"))
    log(Logging(Email()).send("hi"))
    log(Retrying(Logging(Sms()), times = 3).send("hi"))
    log(Uppercase(Retrying(Email(), times = 2)).send("hi"))

    section("2. Swap behaviour without touching OrderService")
    val quiet = OrderService(Sms())
    val loud = OrderService(Logging(Retrying(Email(), times = 2)))
    log(quiet.ship(7))
    log(loud.ship(7))
    note("5 small classes cover every combination. Adding a feature = adding ONE class.")
}
