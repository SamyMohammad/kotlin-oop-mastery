package m08_sealed_enums

import common.log
import common.note
import common.section

/*
 * L02 - Sealed classes
 *
 * A sealed class has a FIXED set of SUBCLASSES (not instances).
 * Each subclass can carry DIFFERENT data. All subclasses must live in the
 * same package and module, so the compiler knows the full list.
 *
 * Enum: fixed set of objects, same shape.
 * Sealed: fixed set of types, each with its own shape.
 */

sealed class ProductsUiState {
    data object Loading : ProductsUiState()
    data class Success(val products: List<String>) : ProductsUiState()
    data class Error(val message: String, val canRetry: Boolean) : ProductsUiState()
}

fun render(state: ProductsUiState): String = when (state) {
    ProductsUiState.Loading -> "spinner"
    is ProductsUiState.Success -> "list of ${state.products.size}: ${state.products.joinToString()}"
    is ProductsUiState.Error -> "error '${state.message}'" + if (state.canRetry) " [Retry]" else ""
}

fun main() {
    section("1. Each state carries its own data")
    val states = listOf(
        ProductsUiState.Loading,
        ProductsUiState.Success(listOf("Pen", "Mug")),
        ProductsUiState.Error("No internet", canRetry = true),
    )
    for (s in states) log(render(s))

    section("2. data object")
    log("toString of a data object: ${ProductsUiState.Loading}")
    note("A plain `object` would print ProductsUiState\$Loading@hash. `data object` prints its name.")

    section("3. Why not an enum?")
    log("An enum constant cannot hold a different product list per use.")
    log("Success(listOf(...)) creates a new value each time - enums cannot.")
}
