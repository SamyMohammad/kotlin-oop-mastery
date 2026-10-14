package m00_mental_model

import common.log
import common.note
import common.section

/*
 * L02 - The four pillars in one screen of code.
 * Each pillar gets its own module later; this is only the map.
 */

// 1) Encapsulation: the balance can only change through deposit()
class GiftCard(initialCents: Long) {
    private var balanceCents = initialCents
    fun deposit(cents: Long) {
        if (cents > 0) balanceCents += cents
    }
    fun balance(): Long = balanceCents
}

// 2) Inheritance: CardPayment IS-A Payment and reuses its code
open class Payment(val amountCents: Long) {
    open fun describe(): String = "payment of $amountCents"
}

class CardPayment(amountCents: Long, private val last4: String) : Payment(amountCents) {
    override fun describe(): String = "card *$last4, " + super.describe()
}

class CashPayment(amountCents: Long) : Payment(amountCents) {
    override fun describe(): String = "cash, " + super.describe()
}

// 4) Abstraction: callers know WHAT a Shipping does, not HOW
interface Shipping {
    fun costCents(weightGrams: Int): Long
}

class FlatShipping : Shipping {
    override fun costCents(weightGrams: Int): Long = 2500
}

class WeightShipping : Shipping {
    override fun costCents(weightGrams: Int): Long = 1000L + weightGrams
}

@Suppress("USELESS_IS_CHECK") // `pay is Payment` is always true - that is the lesson
fun main() {
    section("1. Encapsulation")
    val card = GiftCard(1000)
    card.deposit(500)
    card.deposit(-9999) // ignored by the object's own rule
    log("gift card balance = ${card.balance()}")
    note("You cannot write card.balanceCents = 0 - it is private.")

    section("2. Inheritance")
    val pay = CardPayment(2000, "4242")
    log(pay.describe())
    log("CardPayment is a Payment? ${pay is Payment}")

    section("3. Polymorphism")
    val payments: List<Payment> = listOf(CardPayment(100, "1111"), CashPayment(200), Payment(300))
    for (p in payments) log(p.describe())
    note("One loop, one call - each object answers in its own way.")

    section("4. Abstraction")
    val options: List<Shipping> = listOf(FlatShipping(), WeightShipping())
    for (s in options) log("${s::class.simpleName}: ${s.costCents(800)} cents for 800g")
    note("The caller only knows the Shipping contract.")
}
