package m12_delegation

import common.log
import common.note
import common.section
import kotlin.properties.Delegates

/*
 * L04 - More built-in delegation tricks
 *
 *  var old by this::new           delegate to ANOTHER property (renames, deprecations)
 *  var x: Int by Delegates.notNull()   like lateinit, but works for primitives
 */

class Settings {
    var currencyCode: String = "EGP"

    @Deprecated("Use currencyCode")
    var currency: String by this::currencyCode // old name keeps working, one source of truth
}

class Session {
    var userId: Int by Delegates.notNull() // lateinit cannot be used with Int
}

fun main() {
    section("1. Delegating to another property")
    val s = Settings()
    @Suppress("DEPRECATION")
    s.currency = "USD"
    log("currencyCode = ${s.currencyCode}")
    note("Old callers write `currency`, the value lands in `currencyCode`.")

    section("2. Delegates.notNull for primitives")
    val session = Session()
    try {
        log(session.userId)
    } catch (e: IllegalStateException) {
        log("read before set: ${e::class.simpleName}")
    }
    session.userId = 77
    log("userId = ${session.userId}")
}
