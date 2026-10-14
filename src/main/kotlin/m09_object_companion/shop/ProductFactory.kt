package m09_object_companion.shop

/*
 * Shop snapshot for module 09: a Product that can only be created through
 * its companion factory, which validates and returns null on bad input.
 */
class Product private constructor(val sku: String, val name: String, val priceCents: Long) {

    override fun toString() = "Product($sku, $name, $priceCents)"

    companion object {
        const val SKU_PREFIX = "KS-"
        private var created = 0

        fun create(sku: String, name: String, priceCents: Long): Product? {
            if (!sku.startsWith(SKU_PREFIX) || name.isBlank() || priceCents < 0) return null
            created++
            return Product(sku, name, priceCents)
        }

        fun createdCount() = created
    }
}

// Extension on the companion: called as Product.sample()
fun Product.Companion.sample(): Product = create("${SKU_PREFIX}000", "Sample Pen", 100)!!
