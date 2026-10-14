package m02_encapsulation.exercises.solutions

import m02_encapsulation.exercises.M02Factory
import m02_encapsulation.exercises.Wallet
import m02_encapsulation.exercises.runChecks

class SolutionWallet(initialCents: Long) : Wallet {

    init {
        require(initialCents >= 0) { "initial balance must not be negative" }
    }

    override var balanceCents: Long = initialCents
        private set

    // Backing property: mutable inside, read-only copy outside
    private val _history = mutableListOf<String>()
    override val history: List<String> get() = _history.toList()

    override fun deposit(amount: Long) {
        require(amount > 0) { "deposit must be positive" }
        balanceCents += amount
        _history += "+$amount"
    }

    override fun withdraw(amount: Long): Boolean {
        if (amount <= 0 || amount > balanceCents) return false
        balanceCents -= amount
        _history += "-$amount"
        return true
    }
}

object FactorySolution : M02Factory {
    override fun wallet(initialCents: Long): Wallet = SolutionWallet(initialCents)
}

fun main() = runChecks(FactorySolution)
