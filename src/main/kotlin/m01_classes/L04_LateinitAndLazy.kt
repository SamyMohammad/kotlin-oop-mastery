package m01_classes

import common.log
import common.note
import common.section

/*
 * L04 - lateinit and lazy
 *
 *  lateinit var : "I promise to set this before reading it" (non-null, var, not primitive)
 *  by lazy      : "compute this on first read, then remember it" (val)
 */

class CheckoutScreen {
    // Set later, e.g. after the user logs in. Not nullable, so no `?.` everywhere.
    lateinit var userName: String

    fun isReady() = this::userName.isInitialized

    fun greet() = "Welcome, $userName"
}

class Catalog(private val names: List<String>) {
    // Expensive work, done only once and only if someone needs it
    val searchIndex: Map<Char, List<String>> by lazy {
        log("   computing search index...")
        names.groupBy { it.first().lowercaseChar() }
    }
}

fun main() {
    section("1. lateinit before and after")
    val screen = CheckoutScreen()
    log("ready? ${screen.isReady()}")
    try {
        screen.greet()
    } catch (e: UninitializedPropertyAccessException) {
        log("reading too early threw ${e::class.simpleName}")
    }
    screen.userName = "Sara"
    log("ready? ${screen.isReady()}")
    log(screen.greet())

    section("2. lazy computes once")
    val catalog = Catalog(listOf("Pen", "Paper", "Mug"))
    log("catalog created, index not built yet")
    log("p -> ${catalog.searchIndex['p']}")
    log("m -> ${catalog.searchIndex['m']}")
    note("'computing search index' printed once even though we read it twice.")

    section("3. Which one to use?")
    log("lateinit : value comes from outside later (DI, lifecycle callback)")
    log("lazy     : value is computed by the class itself, on demand")
}
