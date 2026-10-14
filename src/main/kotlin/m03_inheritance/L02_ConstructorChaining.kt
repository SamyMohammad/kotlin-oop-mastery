package m03_inheritance

import common.log
import common.note
import common.section

/*
 * L02 - Constructor chaining across a hierarchy
 *
 *  - The subclass header must call a parent constructor:  class B(x: Int) : A(x)
 *  - The PARENT is fully initialized before the child's initializers run.
 *  - A subclass with no primary constructor calls super(...) from each secondary one.
 */

open class Account(val owner: String) {
    init {
        log("Account init: owner=$owner")
    }
}

class SellerAccount(owner: String, val storeName: String) : Account(owner) {
    init {
        log("SellerAccount init: store=$storeName")
    }
}

// No primary constructor: every secondary constructor calls super(...)
class GuestAccount : Account {
    constructor() : super("guest") {
        log("GuestAccount() body")
    }

    constructor(sessionId: Int) : super("guest-$sessionId") {
        log("GuestAccount(sessionId) body")
    }
}

fun main() {
    section("1. Parent first, then child")
    SellerAccount("Sara", "Sara's Mugs")
    note("Account init printed before SellerAccount init.")

    section("2. Secondary constructors calling super")
    GuestAccount()
    GuestAccount(77)
    note("Each secondary constructor chooses which parent constructor to call.")
}
