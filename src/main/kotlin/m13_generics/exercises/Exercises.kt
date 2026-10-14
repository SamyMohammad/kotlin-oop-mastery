package m13_generics.exercises

/*
 * Module 13 exercises.
 *
 * Tip for stack(): class ListStack<T> : GenericStack<T> { private val items = mutableListOf<T>() ... }
 * Tip for booksOnly(): write a private `inline fun <reified T> List<Any>.onlyOf(): List<T>` and call onlyOf<Book>().
 */

object MyOps : M13Ops {
    override fun <T> stack(): GenericStack<T> = TODO()

    /** Loop and compare with `>` (works because T : Comparable<T>). */
    override fun <T : Comparable<T>> maxOf(items: List<T>): T? = TODO()

    override fun booksOnly(items: List<Any>): List<Book> = TODO()

    /** One line: p.produce().name */
    override fun nameFrom(p: Producer<Item>): String = TODO()

    /** One line: c.consume(book) */
    override fun feed(c: Consumer<Book>, book: Book): String = TODO()
}

fun main() = runChecks(MyOps)
