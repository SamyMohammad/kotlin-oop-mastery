package m13_generics.exercises

import common.check
import common.runCheckSuite

interface Producer<out T> { fun produce(): T }
interface Consumer<in T> { fun consume(item: T): String }

open class Item(val name: String)
class Book(name: String, val pages: Int) : Item(name)

interface M13Ops {
    /** Generic stack: push, pop (null when empty), peek, size. */
    fun <T> stack(): GenericStack<T>
    /** Largest element by natural order; null for empty list. Constraint T : Comparable<T>. */
    fun <T : Comparable<T>> maxOf(items: List<T>): T?
    /** Keep only elements of type T (reified). */
    fun booksOnly(items: List<Any>): List<Book>
    /** Must compile thanks to variance: a Producer<Book> used where Producer<Item> is expected. Return produce().name */
    fun nameFrom(p: Producer<Item>): String
    /** A Consumer<Item> passed where Consumer<Book> is expected. Return c.consume(book). */
    fun feed(c: Consumer<Book>, book: Book): String
}

interface GenericStack<T> { fun push(x: T); fun pop(): T?; fun peek(): T?; val size: Int }

fun runChecks(ops: M13Ops) = runCheckSuite("m13 checks") {
    check("stack lifo") {
        val s = ops.stack<Int>(); s.push(1); s.push(2); s.pop() == 2 && s.peek() == 1 && s.size == 1
    }
    check("stack empty pop") { ops.stack<String>().pop() == null }
    check("maxOf") { ops.maxOf(listOf(3, 9, 2)) == 9 && ops.maxOf(emptyList<String>()) == null }
    check("booksOnly") { ops.booksOnly(listOf(Item("pen"), Book("kotlin", 300), "x")).map { it.name } == listOf("kotlin") }
    check("covariance") {
        val p: Producer<Book> = object : Producer<Book> { override fun produce() = Book("b", 1) }
        ops.nameFrom(p) == "b"
    }
    check("contravariance") {
        val c: Consumer<Item> = object : Consumer<Item> { override fun consume(item: Item) = "got ${item.name}" }
        ops.feed(c, Book("k", 1)) == "got k"
    }
}
