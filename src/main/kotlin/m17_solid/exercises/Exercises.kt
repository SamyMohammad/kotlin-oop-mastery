package m17_solid.exercises

/*
 * Module 17 exercise: refactor the legacy checkout below into the M17Factory contract.
 *
 * The legacy code (commented out so it does not compile against real classes) breaks:
 *   SRP - one class validates, prices, saves, emails
 *   OCP - every new coupon type means editing the `when`
 *   DIP - it creates MySqlOrders() and EmailSender() itself, so it cannot be tested
 *
 * class LegacyCheckout {
 *     private val db = MySqlOrders()            // concrete dependency, created inside
 *     private val mailer = EmailSender()        // concrete dependency, created inside
 *
 *     fun place(id: Int, lineCents: List<Long>, couponType: String): Long {
 *         if (lineCents.isEmpty()) throw IllegalArgumentException("empty order")
 *         val subtotal = lineCents.sum()
 *         val discount = when (couponType) {     // edit here for every new coupon
 *             "NONE" -> 0L
 *             "BIG_ORDER" -> if (subtotal > 100_000) 10_000L else 0L
 *             else -> 0L
 *         }
 *         val total = (subtotal - discount).coerceAtLeast(0)
 *         db.connect()
 *         db.execute("INSERT INTO orders VALUES ($id, $total)")
 *         mailer.smtpLogin("shop", "secret")
 *         mailer.sendMail("Order $id: $total")
 *         return total
 *     }
 * }
 *
 * Your job: a Checkout that RECEIVES OrderStore, Notifier and DiscountPolicy through
 * its constructor, plus two DiscountPolicy values. Then the checks can pass in fakes.
 */

object MyFactory : M17Factory {
    /** Return your refactored checkout class, built only from the three parameters. */
    override fun checkout(store: OrderStore, notifier: Notifier, policy: DiscountPolicy): Checkout = TODO()

    override fun noDiscount(): DiscountPolicy = TODO()

    override fun overThousandGets100Off(): DiscountPolicy = TODO()
}

fun main() = runChecks(MyFactory)
