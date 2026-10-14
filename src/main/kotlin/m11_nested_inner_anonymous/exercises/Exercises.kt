package m11_nested_inner_anonymous.exercises

/*
 * Module 11 exercises.
 *
 * Tip for screen():
 *   class Screen(var title: String) {
 *       inner class TitleClick : ClickListener { override fun onClick() = "clicked on $title" }
 *   }
 *   val s = Screen(title); return Pair({ t -> s.title = t }, s.TitleClick())
 */

object MyFactory : M11Factory {
    /** return object : Comparator<String> { override fun compare(a: String, b: String): Int = ... } */
    override fun byLengthThenAlpha(): Comparator<String> = TODO()

    /** The listener must be an `inner` class of your Screen class. */
    override fun screen(title: String): Pair<(String) -> Unit, ClickListener> = TODO()

    /** An object expression with its own `var count`. */
    override fun counting(): ClickListener = TODO()
}

fun main() = runChecks(MyFactory)
