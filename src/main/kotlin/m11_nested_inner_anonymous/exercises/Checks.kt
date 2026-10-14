package m11_nested_inner_anonymous.exercises

import common.check
import common.runCheckSuite

interface ClickListener { fun onClick(): String }

interface M11Factory {
    /** Return an anonymous object (object expression) implementing Comparator<String>
     *  that sorts by length, then alphabetically. */
    fun byLengthThenAlpha(): Comparator<String>
    /** Screen with title; returns a listener from an INNER class that reads the outer title:
     *  onClick() -> "clicked on <title>" and reflects later title changes. */
    fun screen(title: String): Pair<(String) -> Unit, ClickListener>
    /** Returns a listener (object expression) counting its own clicks: "click #1", "click #2", ... */
    fun counting(): ClickListener
}

fun runChecks(f: M11Factory) = runCheckSuite("m11 checks") {
    check("comparator order") {
        listOf("pear", "fig", "apple", "kiwi").sortedWith(f.byLengthThenAlpha()) == listOf("fig", "kiwi", "pear", "apple")
    }
    check("comparator is anonymous") { f.byLengthThenAlpha()::class.java.isAnonymousClass }
    check("inner sees outer") { f.screen("Cart").second.onClick() == "clicked on Cart" }
    check("inner sees updates") {
        val (setTitle, l) = f.screen("Cart"); setTitle("Checkout"); l.onClick() == "clicked on Checkout"
    }
    check("listener is inner class") {
        val c = f.screen("x").second::class.java
        c.isMemberClass && !java.lang.reflect.Modifier.isStatic(c.modifiers)
    }
    check("counting listener") { val c = f.counting(); c.onClick(); c.onClick() == "click #2" }
}
