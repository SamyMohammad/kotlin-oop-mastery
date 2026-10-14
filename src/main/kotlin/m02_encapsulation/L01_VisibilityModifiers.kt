package m02_encapsulation

import common.log
import common.note
import common.section

/*
 * L01 - Visibility modifiers
 *
 *  public    (default) visible everywhere
 *  internal  visible inside the same Gradle module (this whole project is one module)
 *  protected visible inside the class and its subclasses (members only)
 *  private   member: inside the class only; top-level: inside this FILE only
 */

// private top-level: other files cannot call it
private fun formatCents(cents: Long) = "EGP %d.%02d".format(cents / 100, cents % 100)

// internal: fine anywhere in this Gradle module, hidden from library users
internal const val SHOP_NAME = "Kotlin Shop"

open class Account(val owner: String) {
    public var nickname: String = owner      // `public` is the default, written here only to show it
    internal var lastLoginDay: Int = 0
    protected var points: Int = 0             // subclasses can use it
    private var passwordHash: String = "x1y2" // nobody else

    fun checkPassword(input: String): Boolean = hash(input) == passwordHash

    private fun hash(s: String) = if (s == "secret") "x1y2" else "other"

    fun pointsLabel() = "$owner has $points points"
}

class VipAccount(owner: String) : Account(owner) {
    fun reward() {
        points += 100 // allowed: protected is visible in subclasses
        // passwordHash = ""  <- does not compile: private to Account
    }
}

fun main() {
    section("1. What outside code can see")
    val acc = VipAccount("Sara")
    acc.nickname = "S"
    acc.lastLoginDay = 14
    log("nickname=${acc.nickname}, lastLoginDay=${acc.lastLoginDay}")
    // acc.points = 5         <- does not compile: protected
    // acc.passwordHash       <- does not compile: private
    log("password ok? ${acc.checkPassword("secret")}")

    section("2. protected from a subclass")
    acc.reward()
    log(acc.pointsLabel())

    section("3. private top-level and internal")
    log("$SHOP_NAME total: ${formatCents(12345)}")
    note("formatCents is private to this file. Another file in the same package cannot call it.")

    section("4. Kotlin vs Java defaults")
    log("Kotlin default = public. Java default = package-private.")
    log("Kotlin has no package-private. Use internal or private top-level instead.")
}
