package m08_sealed_enums

import common.log
import common.note
import common.section

/*
 * L01 - Enum classes
 *
 * An enum = a FIXED set of instances, known at compile time.
 * Each constant is a single object. Enums can have:
 *   - constructor properties
 *   - methods
 *   - abstract methods implemented per constant
 *   - interfaces
 */

interface HasIcon {
    val icon: String
}

enum class PaymentMethod(val feePercent: Int) : HasIcon {
    CARD(2) {
        override fun describe() = "card, $feePercent% fee"
        override val icon = "[card]"
    },
    WALLET(1) {
        override fun describe() = "e-wallet, $feePercent% fee"
        override val icon = "[wallet]"
    },
    CASH(0) {
        override fun describe() = "cash on delivery"
        override val icon = "[cash]"
    };

    abstract fun describe(): String

    fun feeFor(amountCents: Long) = amountCents * feePercent / 100
}

fun main() {
    section("1. entries, name, ordinal")
    for (m in PaymentMethod.entries) log("${m.ordinal} ${m.name} ${m.icon} -> ${m.describe()}")

    section("2. Properties and methods")
    log("fee on 10000 by CARD = ${PaymentMethod.CARD.feeFor(10_000)}")

    section("3. valueOf")
    log("valueOf(\"WALLET\") = ${PaymentMethod.valueOf("WALLET")}")
    try {
        PaymentMethod.valueOf("BITCOIN")
    } catch (e: IllegalArgumentException) {
        log("valueOf(\"BITCOIN\") threw ${e::class.simpleName}")
    }

    section("4. Each constant is a singleton")
    log("CARD === CARD ? ${if (PaymentMethod.CARD === PaymentMethod.valueOf("CARD")) "same" else "different"}")
    note("Enums are perfect when the set is fixed AND every case has the same shape.")
}
