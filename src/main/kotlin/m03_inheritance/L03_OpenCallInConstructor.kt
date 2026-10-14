package m03_inheritance

import common.log
import common.note
import common.section

/*
 * L03 - The open-call-in-constructor trap
 *
 * The parent's init runs BEFORE the child's properties are initialized.
 * If the parent's init calls an open function that the child overrides,
 * the override runs on a half-built object and sees null / 0.
 */

open class Screen {
    init {
        log("Screen init calls render()")
        render() // DANGER: open member called from a constructor
    }

    open fun render() {
        log("Screen.render()")
    }
}

class ProductScreen : Screen() {
    private val title: String = "Blue Pen"
    private val stock: Int = 12

    override fun render() {
        // runs from Screen's init, before title and stock are assigned
        log("ProductScreen.render(): title=$title stock=$stock")
    }
}

// The fix: no open calls during construction. Build first, then render.
open class SafeScreen {
    open fun render() = log("SafeScreen.render()")
}

class SafeProductScreen : SafeScreen() {
    private val title: String = "Blue Pen"
    override fun render() = log("SafeProductScreen.render(): title=$title")
}

fun main() {
    section("1. The trap")
    val screen = ProductScreen()
    log("--- object finished, render() again ---")
    screen.render()
    note("First call saw title=null, stock=0 even though both are non-null vals.")

    section("2. The fix")
    val safe = SafeProductScreen()
    safe.render()
    note("Construct completely, then call open members. IntelliJ warns: 'Calling non-final function in constructor'.")
}
