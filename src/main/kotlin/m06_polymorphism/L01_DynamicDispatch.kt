package m06_polymorphism

import common.log
import common.note
import common.section
import m06_polymorphism.shop.EmailNotifier
import m06_polymorphism.shop.Notifier
import m06_polymorphism.shop.PushNotifier
import m06_polymorphism.shop.SmsNotifier

/*
 * L01 - Dynamic dispatch
 *
 * STATIC type  = the type the compiler sees (the variable's declared type)
 * RUNTIME type = the class of the object actually in memory
 *
 * MEMBER functions are chosen by the RUNTIME type   (dynamic dispatch)
 * EXTENSION functions are chosen by the STATIC type (static dispatch)
 */

class UrgentSms : SmsNotifier() {
    override fun send(to: String, msg: String) = "URGENT " + super.send(to, msg.uppercase())
}

// Two extensions with the same name, on the parent and on the child
fun SmsNotifier.kind() = "plain sms"
fun UrgentSms.kind() = "urgent sms"

fun main() {
    section("1. One call, many behaviours")
    val notifiers: List<Notifier> = listOf(SmsNotifier(), EmailNotifier(), PushNotifier(), UrgentSms())
    for (n in notifiers) log(n.send("Sara", "your order shipped"))
    note("The loop only knows Notifier. Each object runs its own send().")

    section("2. Static type vs runtime type")
    val n: SmsNotifier = UrgentSms()
    log("static type  = SmsNotifier")
    log("runtime type = ${n::class.simpleName}")
    log("member send(): ${n.send("Omar", "hi")}")

    section("3. Extensions do NOT dispatch dynamically")
    log("n.kind() = ${n.kind()}")
    val u: UrgentSms = n as UrgentSms // the SAME object, now declared as UrgentSms
    log("u.kind() = ${u.kind()}")
    note("Same object both times. The extension was picked from the declared type.")
}
