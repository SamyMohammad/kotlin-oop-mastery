package m13_generics

import common.log
import common.note
import common.section

/*
 * L03 - Variance: in, out, invariant
 *
 * Book is an Item. Is a Box<Book> a Box<Item>? It depends on what the box can do.
 *
 *  INVARIANT  class MutableBox<T>      read AND write T -> no subtyping either way
 *  COVARIANT  interface Source<out T>  only RETURNS T   -> Source<Book> is a Source<Item>
 *  CONTRAVARIANT interface Sink<in T>  only TAKES T     -> Sink<Item> is a Sink<Book>
 *
 * Mnemonic: Producer OUT, Consumer IN.
 *
 * Why MutableList<Book> cannot be a MutableList<Item>:
 *   val items: MutableList<Item> = books   // if this were allowed...
 *   items.add(Item("pen"))                 // ...a plain Item would sit inside a list of Books
 */

open class Item(val name: String)
class Book(name: String) : Item(name)

interface Source<out T> {
    fun next(): T
}

interface Sink<in T> {
    fun put(item: T): String
}

class BookShelf : Source<Book> {
    override fun next() = Book("Kotlin in Action")
}

class AnyItemBin : Sink<Item> {
    override fun put(item: Item) = "binned ${item.name}"
}

fun readName(source: Source<Item>) = source.next().name
fun storeBook(sink: Sink<Book>) = sink.put(Book("Clean Code"))

fun main() {
    section("1. out: a producer of Books is a producer of Items")
    log(readName(BookShelf()))

    section("2. in: a consumer of Items is a consumer of Books")
    log(storeBook(AnyItemBin()))

    section("3. Invariant MutableList")
    val books: MutableList<Book> = mutableListOf(Book("A"))
    // val items: MutableList<Item> = books   <- error: type mismatch
    val readOnly: List<Item> = books          // OK: List<out E> is covariant
    log("read-only view sees ${readOnly.size} item(s)")

    section("4. Use-site projection")
    fun printAll(items: Array<out Item>) = items.joinToString { it.name }
    log(printAll(arrayOf(Book("X"), Book("Y"))))
    note("Array<out Item> = 'I will only read from this array', so Array<Book> is accepted.")
}
