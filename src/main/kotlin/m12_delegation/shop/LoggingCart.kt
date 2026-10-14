package m12_delegation.shop

/*
 * Shop snapshot for module 12: add logging to ANY cart implementation
 * without inheriting from it - by delegating to it.
 */
interface CartApi {
    fun add(name: String, qty: Int)
    fun remove(name: String)
    fun lines(): Map<String, Int>
}

class InMemoryCart : CartApi {
    private val items = linkedMapOf<String, Int>()
    override fun add(name: String, qty: Int) {
        items[name] = (items[name] ?: 0) + qty
    }
    override fun remove(name: String) {
        items.remove(name)
    }
    override fun lines(): Map<String, Int> = items.toMap()
}

class LoggingCart(
    private val inner: CartApi,
    private val log: (String) -> Unit,
) : CartApi by inner {
    // Only add() is decorated; remove() and lines() are forwarded automatically
    override fun add(name: String, qty: Int) {
        log("add $name x$qty")
        inner.add(name, qty)
    }
}
