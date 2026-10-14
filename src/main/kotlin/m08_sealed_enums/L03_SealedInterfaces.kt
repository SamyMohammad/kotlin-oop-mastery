package m08_sealed_enums

import common.log
import common.note
import common.section

/*
 * L03 - Sealed interfaces
 *
 * Like a sealed class, but:
 *   - no constructor / state in the parent
 *   - a class can implement SEVERAL sealed interfaces
 *   - enums and objects can implement them too
 * The subtypes may be spread across files of the same package and module.
 */

sealed interface CheckoutError
sealed interface Retryable

data class NetworkDown(val seconds: Int) : CheckoutError, Retryable
data class CardDeclined(val reason: String) : CheckoutError
data object OutOfStock : CheckoutError
data object ServerBusy : CheckoutError, Retryable

fun userMessage(e: CheckoutError): String = when (e) {
    is NetworkDown -> "No connection for ${e.seconds}s"
    is CardDeclined -> "Card declined: ${e.reason}"
    OutOfStock -> "Sorry, out of stock"
    ServerBusy -> "Server busy"
}

fun main() {
    section("1. One hierarchy for errors")
    val errors = listOf(NetworkDown(5), CardDeclined("insufficient funds"), OutOfStock, ServerBusy)
    for (e in errors) log(userMessage(e))

    section("2. A second sealed interface cuts across the first")
    for (e in errors) log("${e::class.simpleName}: retry? ${e is Retryable}")
    note("NetworkDown and ServerBusy implement two sealed interfaces at once. A sealed class could not.")
}
