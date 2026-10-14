package m03_inheritance.shop

/*
 * Shop snapshot for module 03: the Payment hierarchy.
 * m04 turns the shared idea into an abstract class, m05 into interfaces.
 */
open class Payment(val amountCents: Long) {
    open fun fee(): Long = 0
    open fun describe(): String = "Payment of $amountCents"
    fun total(): Long = amountCents + fee()
}

class CardPayment(amountCents: Long, val last4: String) : Payment(amountCents) {
    // 2% fee, integer cents rounded down
    override fun fee(): Long = amountCents * 2 / 100
    override fun describe(): String = "Card *$last4 - " + super.describe()
}

class CashOnDelivery(amountCents: Long) : Payment(amountCents) {
    override fun fee(): Long = 1500
    override fun describe(): String = "Cash on delivery - " + super.describe()
}
