package m10_value_classes.exercises

/*
 * Module 10 exercises. Write your own Money value class:
 *
 *   @JvmInline
 *   value class Money(val cents: Long) {
 *       init { require(cents >= 0) }
 *       operator fun plus(other: Money) = ...
 *       ...
 *   }
 *
 * Then replace `Any` below with Money: object MyOps : MoneyOps<Money>
 */

object MyOps : MoneyOps<Any> {
    override fun of(cents: Long): Any = TODO()
    override fun plus(a: Any, b: Any): Any = TODO()
    override fun discount(m: Any, percent: Int): Any = TODO()
    override fun format(m: Any): String = TODO()

    /** Return Money::class */
    override fun type(): kotlin.reflect.KClass<*> = TODO()
}

fun main() = runChecks(MyOps)
