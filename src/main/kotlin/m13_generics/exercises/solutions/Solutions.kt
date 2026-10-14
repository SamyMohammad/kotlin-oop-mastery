package m13_generics.exercises.solutions

import m13_generics.exercises.Book
import m13_generics.exercises.Consumer
import m13_generics.exercises.GenericStack
import m13_generics.exercises.Item
import m13_generics.exercises.M13Ops
import m13_generics.exercises.Producer
import m13_generics.exercises.runChecks

class ListStack<T> : GenericStack<T> {
    private val items = mutableListOf<T>()
    override fun push(x: T) {
        items += x
    }
    override fun pop(): T? = items.removeLastOrNull()
    override fun peek(): T? = items.lastOrNull()
    override val size: Int get() = items.size
}

// reified: T is known at runtime inside this inline function, so `is T` works
private inline fun <reified T> List<Any>.onlyOf(): List<T> = filter { it is T }.map { it as T }

object OpsSolution : M13Ops {
    override fun <T> stack(): GenericStack<T> = ListStack()

    override fun <T : Comparable<T>> maxOf(items: List<T>): T? {
        var best: T? = null
        for (x in items) if (best == null || x > best) best = x
        return best
    }

    override fun booksOnly(items: List<Any>): List<Book> = items.onlyOf<Book>()

    // Producer<out T>: a Producer<Book> IS a Producer<Item>, so callers may pass one here
    override fun nameFrom(p: Producer<Item>): String = p.produce().name

    // Consumer<in T>: a Consumer<Item> IS a Consumer<Book>, so callers may pass one here
    override fun feed(c: Consumer<Book>, book: Book): String = c.consume(book)
}

fun main() = runChecks(OpsSolution)
