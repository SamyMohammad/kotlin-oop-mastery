package m07_data_classes.exercises.solutions

import m07_data_classes.exercises.CartUiState
import m07_data_classes.exercises.M07Ops
import m07_data_classes.exercises.runChecks

object OpsSolution : M07Ops {
    override fun increment(state: CartUiState, name: String): CartUiState {
        val exists = state.items.any { (n, _) -> n == name }
        val newItems =
            if (exists) state.items.map { (n, q) -> if (n == name) n to q + 1 else n to q }
            else state.items + (name to 1)
        return state.copy(items = newItems, error = null)
    }

    override fun fail(state: CartUiState, message: String): CartUiState =
        state.copy(isLoading = false, error = message)

    override fun totalQty(state: CartUiState): Int {
        var total = 0
        for ((_, qty) in state.items) total += qty // destructuring a Pair
        return total
    }
}

fun main() = runChecks(OpsSolution)
