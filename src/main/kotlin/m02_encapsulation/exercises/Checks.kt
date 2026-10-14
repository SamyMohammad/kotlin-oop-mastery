package m02_encapsulation.exercises

import common.check
import common.runCheckSuite

interface Wallet {
    /** Read-only from outside. Starts at the value passed to the factory. */
    val balanceCents: Long
    /** Adds money. amount <= 0 -> IllegalArgumentException. */
    fun deposit(amount: Long)
    /** Returns false (and changes nothing) when amount > balance or amount <= 0. */
    fun withdraw(amount: Long): Boolean
    /** Every successful deposit/withdraw as "+500" / "-200", oldest first. Must be a copy. */
    val history: List<String>
}

interface M02Factory {
    /** initialCents < 0 -> IllegalArgumentException */
    fun wallet(initialCents: Long): Wallet
}

fun runChecks(f: M02Factory) = runCheckSuite("m02 checks") {
    check("starts with initial balance") { f.wallet(1000).balanceCents == 1000L }
    check("negative initial rejected") {
        runCatching { f.wallet(-1) }.exceptionOrNull() is IllegalArgumentException
    }
    check("deposit adds") { f.wallet(0).apply { deposit(500) }.balanceCents == 500L }
    check("deposit zero rejected") {
        runCatching { f.wallet(0).deposit(0) }.exceptionOrNull() is IllegalArgumentException
    }
    check("withdraw too much refused") {
        val w = f.wallet(100); !w.withdraw(200) && w.balanceCents == 100L
    }
    check("history records in order") {
        val w = f.wallet(0); w.deposit(500); w.withdraw(200); w.withdraw(9999)
        w.history == listOf("+500", "-200")
    }
    check("history is a defensive copy") {
        val w = f.wallet(0); w.deposit(1)
        // toList() may return an unmodifiable list, so the attack itself may throw - ignore that
        runCatching { (w.history as? MutableList<String>)?.add("+999") }
        w.history == listOf("+1")
    }
}
