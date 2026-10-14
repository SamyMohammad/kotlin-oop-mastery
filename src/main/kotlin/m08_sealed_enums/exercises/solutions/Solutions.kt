package m08_sealed_enums.exercises.solutions

import m08_sealed_enums.exercises.M08Ops
import m08_sealed_enums.exercises.PaymentResult
import m08_sealed_enums.exercises.Tier
import m08_sealed_enums.exercises.runChecks

object OpsSolution : M08Ops {
    // No else: adding a new PaymentResult subtype will break this line at compile time - good
    override fun message(result: PaymentResult): String = when (result) {
        is PaymentResult.Success -> "Paid (${result.transactionId})"
        is PaymentResult.Declined -> "Declined: ${result.reason}"
        PaymentResult.NetworkError -> "Try again"
    }

    override fun tierFor(spentCents: Long): Tier = Tier.entries.last { spentCents >= it.minSpendCents }

    override fun next(tier: Tier): Tier? = Tier.entries.getOrNull(tier.ordinal + 1)
}

fun main() = runChecks(OpsSolution)
