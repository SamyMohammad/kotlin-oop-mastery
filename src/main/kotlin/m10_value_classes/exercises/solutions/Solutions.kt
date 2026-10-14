package m10_value_classes.exercises.solutions

import m10_value_classes.exercises.MoneyOps
import m10_value_classes.exercises.runChecks

@JvmInline
value class Money(val cents: Long) {
    init {
        require(cents >= 0) { "money must not be negative" }
    }

    operator fun plus(other: Money) = Money(cents + other.cents)

    fun discount(percent: Int): Money {
        require(percent in 0..100) { "percent must be 0..100" }
        return Money(cents * (100 - percent) / 100) // the RESULT is rounded down
    }

    fun format() = "EGP %d.%02d".format(cents / 100, cents % 100)
}

object OpsSolution : MoneyOps<Money> {
    override fun of(cents: Long) = Money(cents)
    override fun plus(a: Money, b: Money) = a + b
    override fun discount(m: Money, percent: Int) = m.discount(percent)
    override fun format(m: Money) = m.format()
    override fun type() = Money::class
}

fun main() = runChecks(OpsSolution)
