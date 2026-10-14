package m09_object_companion

import common.log
import common.note
import common.section

/*
 * L01 - object declarations (singletons)
 *
 *  object X { ... }   declares a class AND its only instance in one go.
 *  - created lazily, the first time it is accessed
 *  - creation is thread-safe (JVM class initialization)
 *  - can have properties, functions, init blocks
 *  - can extend a class and implement interfaces
 */

interface Logger {
    fun log(message: String)
}

object AppConfig {
    init {
        log("AppConfig init runs (only once)")
    }

    val currency = "EGP"
    var darkMode = false
}

object ConsoleLogger : Logger {
    private var lines = 0
    override fun log(message: String) {
        lines++
        common.log("LOG #$lines: $message")
    }
}

fun process(logger: Logger) = logger.log("order processed")

fun main() {
    section("1. Lazy creation")
    log("main started - AppConfig not created yet")
    log("currency = ${AppConfig.currency}")
    log("darkMode = ${AppConfig.darkMode}")
    AppConfig.darkMode = true
    log("darkMode after change = ${AppConfig.darkMode}")
    note("The init block printed once, at first access - not at program start.")

    section("2. One instance only")
    val a = AppConfig
    val b = AppConfig
    log("a and b: ${if (a === b) "same" else "different"}")

    section("3. An object can implement an interface")
    process(ConsoleLogger)
    process(ConsoleLogger)
    note("ConsoleLogger is passed like any other Logger value.")
}
