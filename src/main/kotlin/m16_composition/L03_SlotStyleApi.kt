package m16_composition

import common.log
import common.note
import common.section

/*
 * L03 - Slot APIs
 *
 * Instead of subclassing a Screen to customise its header/footer,
 * the screen takes the parts as FUNCTIONS (slots). The caller fills them in.
 * Optional slots default to null / empty.
 *
 * This is a plain-Kotlin version of Compose's Scaffold(topBar = { }, bottomBar = { }) { }.
 */

fun renderScreen(
    header: (() -> String)? = null,
    footer: (() -> String)? = null,
    body: () -> String,
): String {
    val lines = mutableListOf<String>()
    header?.let { lines += "== ${it()} ==" }
    lines += body()
    footer?.let { lines += "-- ${it()} --" }
    return lines.joinToString("\n")
}

// A reusable wrapper built by COMPOSING renderScreen, not by inheriting from it
fun renderShopScreen(title: String, body: () -> String) =
    renderScreen(header = { "Kotlin Shop / $title" }, footer = { "Free delivery over EGP 500" }, body = body)

fun main() {
    section("1. All slots filled")
    for (line in renderScreen(header = { "Cart" }, footer = { "Checkout" }) { "2 items, EGP 50.00" }.lines()) log(line)

    section("2. Only the body")
    log(renderScreen { "Nothing in your cart yet" })

    section("3. A wrapper made by composition")
    for (line in renderShopScreen("Product") { "Blue Pen - EGP 5.00" }.lines()) log(line)
    note("renderShopScreen reuses renderScreen by calling it. No base class, no override.")
}
