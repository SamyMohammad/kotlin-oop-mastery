package m07_data_classes.exercises

import common.check
import common.runCheckSuite

/** Given. The learner works with this type. */
data class CartUiState(
    val items: List<Pair<String, Int>> = emptyList(), // name to qty
    val isLoading: Boolean = false,
    val error: String? = null,
)

interface M07Ops {
    /** Returns a new state with qty for name increased by 1 (added with qty 1 if absent, appended at end). Clears error. */
    fun increment(state: CartUiState, name: String): CartUiState
    /** Returns a new state with isLoading=false and error set. Items unchanged. */
    fun fail(state: CartUiState, message: String): CartUiState
    /** Total quantity using destructuring in the loop/lambda. */
    fun totalQty(state: CartUiState): Int
}

fun runChecks(ops: M07Ops) = runCheckSuite("m07 checks") {
    val s0 = CartUiState(isLoading = true, error = "old")
    check("increment adds new") { ops.increment(CartUiState(), "Pen").items == listOf("Pen" to 1) }
    check("increment existing") {
        ops.increment(CartUiState(listOf("Pen" to 1, "Mug" to 2)), "Pen").items == listOf("Pen" to 2, "Mug" to 2)
    }
    check("increment clears error") { ops.increment(s0, "Pen").error == null }
    check("original untouched") { ops.increment(s0, "Pen"); s0 == CartUiState(isLoading = true, error = "old") }
    check("fail sets error") { ops.fail(s0, "boom") == CartUiState(isLoading = false, error = "boom") }
    check("total qty") { ops.totalQty(CartUiState(listOf("A" to 2, "B" to 3))) == 5 }
}
