package m07_data_classes.shop

/*
 * Shop snapshot for module 07: Product becomes an immutable data class.
 * Changing a product means making a new one with copy().
 */
data class Product(
    val id: Int,
    val name: String,
    val priceCents: Long,
    val tags: List<String> = emptyList(),
) {
    init {
        require(priceCents >= 0) { "price must not be negative" }
    }

    fun discounted(percent: Int): Product = copy(priceCents = priceCents - priceCents * percent / 100)
}
