package m14_extensions_receivers

import common.log
import common.note
import common.section

/*
 * L01 - Extension functions
 *
 *  fun Long.toEgp(): String = ...
 *
 * Looks like a new member of Long, but it is a static function in disguise:
 *     public static String toEgp(long $this) { ... }     (in the file class, e.g. L01_ExtensionFunctionsKt)
 *
 * Rules:
 *  - cannot see private/protected members of the receiver
 *  - resolved statically (by declared type), see m06
 *  - if a MEMBER with the same signature exists, the member always wins
 *  - the receiver can be nullable: fun String?.orDash()
 */

fun Long.toEgp(): String = "EGP %d.%02d".format(this / 100, this % 100)

fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this.trim()

class Coupon(val code: String) {
    fun describe() = "member describe: $code"
}

// Same signature as the member: never called on a Coupon
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun Coupon.describe() = "extension describe"

// A new name works fine
fun Coupon.shout() = code.uppercase() + "!"

fun main() {
    section("1. Extending a type you don't own")
    log(1250L.toEgp())
    log(7L.toEgp())

    section("2. Nullable receiver")
    val missing: String? = null
    log("null    -> ${missing.orDash()}")
    log("blank   -> ${"   ".orDash()}")
    log("padded  -> ${"  Sara  ".orDash()}")

    section("3. Member always wins")
    val c = Coupon("save10")
    log(c.describe())
    log(c.shout())
    note("Extensions add behaviour; they can never replace or override what a class already has.")
}
