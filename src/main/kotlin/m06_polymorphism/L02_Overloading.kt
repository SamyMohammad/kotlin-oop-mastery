package m06_polymorphism

import common.log
import common.note
import common.section
import m06_polymorphism.shop.Notifier
import m06_polymorphism.shop.SmsNotifier

/*
 * L02 - Overloading (compile-time polymorphism)
 *
 * Overloads = same name, different parameter lists.
 * The compiler picks the overload from the STATIC types of the arguments,
 * before the program runs. That is the opposite of overriding.
 */

fun describe(n: Notifier) = "describe(Notifier)"
fun describe(n: SmsNotifier) = "describe(SmsNotifier)"

fun priceLabel(cents: Long) = "EGP ${cents / 100}"
fun priceLabel(cents: Long, currency: String) = "$currency ${cents / 100}"
fun priceLabel(amount: Double) = "EGP %.2f".format(amount)

// Default parameter instead of two overloads
fun greeting(name: String, vip: Boolean = false) = if (vip) "Welcome back, $name!" else "Hello, $name"

fun main() {
    section("1. Overload chosen by parameter type and count")
    log(priceLabel(12_500L))
    log(priceLabel(12_500L, "USD"))
    log(priceLabel(125.5))

    section("2. Chosen at COMPILE time, from the static type")
    val asBase: Notifier = SmsNotifier()
    val asSms = SmsNotifier()
    log("declared Notifier:    ${describe(asBase)}")
    log("declared SmsNotifier: ${describe(asSms)}")
    note("Both objects are SmsNotifier at runtime. Only the declared type differed.")

    section("3. Default arguments replace most overloads")
    log(greeting("Sara"))
    log(greeting("Omar", vip = true))
    note("For Java callers, @JvmOverloads generates the overloads from defaults.")
}
