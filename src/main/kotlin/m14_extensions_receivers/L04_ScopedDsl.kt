package m14_extensions_receivers

import common.log
import common.note
import common.section

/*
 * L04 - Scoped extensions and @DslMarker
 *
 * An extension declared INSIDE a class or interface is a member extension:
 * it can only be called where that class is the receiver - i.e. inside its scope.
 * This is exactly how Compose's RowScope.weight works.
 *
 * @DslMarker stops an inner block from silently calling the outer block's functions.
 */

data class Cell(val text: String, val weight: Int = 1)

@DslMarker
annotation class LayoutDsl

@LayoutDsl
class RowBuilder {
    val cells = mutableListOf<Cell>()

    fun cell(text: String) = Cell(text).also { cells += it }

    // Member extension: Cell.weight(...) exists ONLY inside a RowBuilder block
    fun Cell.weight(w: Int) {
        cells[cells.indexOf(this)] = copy(weight = w)
    }
}

@LayoutDsl
class ScreenBuilder {
    val rows = mutableListOf<List<Cell>>()
    fun row(block: RowBuilder.() -> Unit) {
        rows += RowBuilder().apply(block).cells
    }
}

fun screen(block: ScreenBuilder.() -> Unit) = ScreenBuilder().apply(block).rows

fun render(rows: List<List<Cell>>): List<String> = rows.map { row ->
    val total = row.sumOf { it.weight }
    row.joinToString(" | ") { "${it.text}(${it.weight * 100 / total}%)" }
}

fun main() {
    section("1. A scoped extension")
    val rows = screen {
        row {
            cell("Image").weight(1)
            cell("Name and price").weight(3)
        }
        row {
            cell("Qty")
            cell("Remove")
            // row { }  <- error with @DslMarker: 'row' can't be called in this context by implicit receiver
        }
    }
    for (line in render(rows)) log(line)
    // Cell("x").weight(2)  <- error outside a row block: Unresolved reference 'weight'

    section("2. Why it matters")
    log("weight() only makes sense inside a row, so it only EXISTS inside a row.")
    note("Same design as Compose: Modifier.weight is only available inside Row { } and Column { }.")
}
