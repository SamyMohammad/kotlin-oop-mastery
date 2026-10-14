package m11_nested_inner_anonymous.exercises.solutions

import m11_nested_inner_anonymous.exercises.ClickListener
import m11_nested_inner_anonymous.exercises.M11Factory
import m11_nested_inner_anonymous.exercises.runChecks

class Screen(var title: String) {
    // `inner`: holds a reference to its Screen, so it always reads the CURRENT title
    inner class TitleClick : ClickListener {
        override fun onClick() = "clicked on $title"
    }
}

object FactorySolution : M11Factory {
    override fun byLengthThenAlpha(): Comparator<String> = object : Comparator<String> {
        override fun compare(a: String, b: String): Int {
            val byLength = a.length.compareTo(b.length)
            return if (byLength != 0) byLength else a.compareTo(b)
        }
    }

    override fun screen(title: String): Pair<(String) -> Unit, ClickListener> {
        val s = Screen(title)
        return Pair({ newTitle -> s.title = newTitle }, s.TitleClick())
    }

    override fun counting(): ClickListener = object : ClickListener {
        private var count = 0 // state lives inside the anonymous object
        override fun onClick() = "click #${++count}"
    }
}

fun main() = runChecks(FactorySolution)
