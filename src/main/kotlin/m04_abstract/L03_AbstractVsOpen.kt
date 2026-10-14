package m04_abstract

import common.log
import common.note
import common.section

/*
 * L03 - open class vs abstract class vs interface
 *
 *                     | can instantiate | holds state | constructor | forces override
 *  open class         | yes             | yes         | yes         | no (defaults)
 *  abstract class     | no              | yes         | yes         | yes (abstract members)
 *  interface          | no              | no          | no          | yes (members w/o body)
 *
 * A class can extend ONE class (open or abstract) but implement MANY interfaces.
 */

// 1) open class: a working default, subclasses MAY change it
open class OpenNotifier {
    open fun send(msg: String) = "default channel: $msg"
}

class QuietNotifier : OpenNotifier() // overrides nothing, still compiles

// 2) abstract class: shared state + forced decision
abstract class AbstractNotifier(private val sender: String) {
    protected abstract fun channel(): String
    fun send(msg: String) = "${channel()} from $sender: $msg"
}

class SmsNotifier : AbstractNotifier("Shop") {
    override fun channel() = "sms"
}

// 3) interface: pure contract, no state
interface Notifier {
    fun send(msg: String): String
}

class PushNotifier : Notifier {
    override fun send(msg: String) = "push: $msg"
}

fun main() {
    section("1. open class - overriding is optional")
    log(OpenNotifier().send("hi"))
    log(QuietNotifier().send("hi"))

    section("2. abstract class - overriding is required, state is shared")
    log(SmsNotifier().send("order shipped"))
    // AbstractNotifier("x")  <- does not compile

    section("3. interface - contract only")
    log(PushNotifier().send("sale starts"))
    note("Pick abstract when subclasses share state or a constructor; interface otherwise.")
}
