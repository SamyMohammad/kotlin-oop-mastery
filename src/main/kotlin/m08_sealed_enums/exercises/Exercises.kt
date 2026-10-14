package m08_sealed_enums.exercises

/*
 * Module 08 exercises. PaymentResult (sealed) and Tier (enum) are in Checks.kt.
 */

object MyOps : M08Ops {
    /** `when (result) { is PaymentResult.Success -> ... }` with NO else - let the compiler check you. */
    override fun message(result: PaymentResult): String = TODO()

    /** Tier.entries is ordered BRONZE, SILVER, GOLD. Find the last one you qualify for. */
    override fun tierFor(spentCents: Long): Tier = TODO()

    /** Use ordinal and Tier.entries.getOrNull(...). */
    override fun next(tier: Tier): Tier? = TODO()
}

fun main() = runChecks(MyOps)
