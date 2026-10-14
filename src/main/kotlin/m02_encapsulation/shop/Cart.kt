package m02_encapsulation.shop

/*
 * Shop snapshot for module 02: an encapsulated Cart.
 * CartLine is a data class here only as a small value holder (explained in m07).
 */
data class CartLine(val productId: Int, val qty: Int)

class Cart {
    private val _items = mutableListOf<CartLine>()

    /** A copy: callers can read, never mutate the cart's own list. */
    val items: List<CartLine> get() = _items.toList()

    val totalQty: Int get() = _items.sumOf { it.qty }

    fun add(productId: Int, qty: Int) {
        require(qty > 0) { "qty must be positive" }
        val index = _items.indexOfFirst { it.productId == productId }
        if (index >= 0) {
            _items[index] = _items[index].copy(qty = _items[index].qty + qty)
        } else {
            _items += CartLine(productId, qty)
        }
    }

    fun remove(productId: Int) {
        _items.removeAll { it.productId == productId }
    }
}
