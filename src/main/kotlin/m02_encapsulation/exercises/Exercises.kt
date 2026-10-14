package m02_encapsulation.exercises

/*
 * Module 02 exercises. Build a Wallet that nobody can corrupt from outside.
 *
 * Tip: write `class MyWallet(initialCents: Long) : Wallet { ... }` with
 *  - a `private set` (or backing property) for the balance
 *  - a private MutableList for history, exposed as a COPY
 */

object MyFactory : M02Factory {
    /** Validate initialCents >= 0 with require, then return your wallet. */
    override fun wallet(initialCents: Long): Wallet = TODO()
}

fun main() = runChecks(MyFactory)
