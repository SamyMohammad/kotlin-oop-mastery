package m07_data_classes.exercises

/*
 * Module 07 exercises. CartUiState (Checks.kt) is an immutable data class.
 * Never mutate it - always return state.copy(...).
 */

object MyOps : M07Ops {
    /** Use copy(items = ..., error = null). Build the new list with map / plus, keep order. */
    override fun increment(state: CartUiState, name: String): CartUiState = TODO()

    /** Use copy(isLoading = false, error = message). */
    override fun fail(state: CartUiState, message: String): CartUiState = TODO()

    /** Destructure each pair: for ((_, qty) in state.items) ... */
    override fun totalQty(state: CartUiState): Int = TODO()
}

fun main() = runChecks(MyOps)
