package m01_classes

import common.log
import common.note
import common.section
import m01_classes.shop.Product

/*
 * L01 - Constructors
 *
 *  - primary constructor: in the class header
 *  - `val` / `var` in the header = parameter AND property
 *  - a plain parameter (no val/var) is only visible in init blocks and initializers
 *  - default and named arguments replace most Java overloads
 *  - secondary constructors must delegate to the primary with this(...)
 */

class Customer(
    val name: String,              // property (read-only)
    var email: String,             // property (read-write)
    phone: String,                 // plain parameter - NOT a property
    val isVip: Boolean = false,    // default value
) {
    // a plain parameter can be used in initializers
    val maskedPhone: String = "*".repeat(phone.length - 3) + phone.takeLast(3)

    // secondary constructor: guest checkout with no phone
    constructor(name: String) : this(name, email = "guest@shop.local", phone = "000") {
        log("secondary constructor ran for $name")
    }

    fun describe() = "$name <$email> phone=$maskedPhone vip=$isVip"
}

fun main() {
    section("1. Primary constructor")
    val sara = Customer("Sara", "sara@mail.com", "01012345678")
    log(sara.describe())
    // sara.phone  <- does not compile: phone is a parameter, not a property

    section("2. Default and named arguments")
    val omar = Customer(name = "Omar", phone = "01198765432", email = "omar@mail.com", isVip = true)
    log(omar.describe())
    note("Named arguments can come in any order. Defaults remove the need for overloads.")

    section("3. var vs val in the header")
    sara.email = "sara@new.com"
    log("after change: ${sara.describe()}")
    // sara.name = "X"  <- does not compile: val has no setter

    section("4. Secondary constructor")
    val guest = Customer("Guest")
    log(guest.describe())

    section("5. The shop Product")
    val pen = Product(1, "Pen", 1000)
    pen.applyDiscount(10)
    log("${pen.name} costs ${pen.priceCents} cents after 10% off")
    try {
        Product(2, "Broken", -5)
    } catch (e: IllegalArgumentException) {
        log("rejected: ${e.message}")
    }
    note("The init block validates every object at birth. No invalid Product can exist.")
}
