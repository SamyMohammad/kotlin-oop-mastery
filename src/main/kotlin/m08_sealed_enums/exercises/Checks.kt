package m08_sealed_enums.exercises

import common.check
import common.runCheckSuite

sealed interface PaymentResult {
    data class Success(val transactionId: String) : PaymentResult
    data class Declined(val reason: String) : PaymentResult
    data object NetworkError : PaymentResult
}

enum class Tier(val minSpendCents: Long) { BRONZE(0), SILVER(100_000), GOLD(500_000) }

interface M08Ops {
    /** Exhaustive when, NO else branch:
     *  Success -> "Paid (<id>)", Declined -> "Declined: <reason>", NetworkError -> "Try again" */
    fun message(result: PaymentResult): String
    /** Highest tier whose minSpendCents <= spent, using Tier.entries. */
    fun tierFor(spentCents: Long): Tier
    /** Next tier or null when GOLD. */
    fun next(tier: Tier): Tier?
}

fun runChecks(ops: M08Ops) = runCheckSuite("m08 checks") {
    check("success msg") { ops.message(PaymentResult.Success("T1")) == "Paid (T1)" }
    check("declined msg") { ops.message(PaymentResult.Declined("no funds")) == "Declined: no funds" }
    check("network msg") { ops.message(PaymentResult.NetworkError) == "Try again" }
    check("tier bronze") { ops.tierFor(99_999) == Tier.BRONZE }
    check("tier silver edge") { ops.tierFor(100_000) == Tier.SILVER }
    check("tier gold") { ops.tierFor(1_000_000) == Tier.GOLD }
    check("next tier") { ops.next(Tier.BRONZE) == Tier.SILVER && ops.next(Tier.GOLD) == null }
}
