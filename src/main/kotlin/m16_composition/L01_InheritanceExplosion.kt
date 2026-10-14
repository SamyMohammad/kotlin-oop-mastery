package m16_composition

import common.log
import common.note
import common.section

/*
 * L01 - The inheritance explosion
 *
 * Features: channel (SMS, Email) x logging (yes/no) x retry (yes/no).
 * With inheritance, every COMBINATION needs its own class.
 */

open class Notifier {
    open fun send(msg: String) = "send: $msg"
}

open class SmsNotifier : Notifier() {
    override fun send(msg: String) = "SMS: $msg"
}

open class EmailNotifier : Notifier() {
    override fun send(msg: String) = "EMAIL: $msg"
}

class LoggingSmsNotifier : SmsNotifier() {
    override fun send(msg: String) = "[log] " + super.send(msg)
}

class LoggingEmailNotifier : EmailNotifier() {
    override fun send(msg: String) = "[log] " + super.send(msg)  // same code again
}

class RetryingLoggingSmsNotifier : SmsNotifier() {
    override fun send(msg: String) = "[retry] [log] " + super.send(msg) // and again
}

fun classesNeeded(channels: Int, optionalFeatures: Int) = channels * (1 shl optionalFeatures)

fun main() {
    section("1. A few of the combinations")
    log(LoggingSmsNotifier().send("order shipped"))
    log(LoggingEmailNotifier().send("order shipped"))
    log(RetryingLoggingSmsNotifier().send("order shipped"))
    note("Logging is copy-pasted into every class that needs it.")

    section("2. How many classes would we need?")
    for (features in 1..4) {
        log("2 channels, $features optional features -> ${classesNeeded(2, features)} classes")
    }
    log("3 channels, 4 optional features -> ${classesNeeded(3, 4)} classes")
    note("Each new feature DOUBLES the class count. L02 needs one class per feature instead.")
}
