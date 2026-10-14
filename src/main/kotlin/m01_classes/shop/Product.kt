package m01_classes.shop

/*
 * Shop snapshot for module 01: the first real Product class.
 * Later modules keep their own evolved copy (data class in m07, Price value class in m10).
 */
class Product(val id: Int, val name: String, priceCents: Long) {

    init {
        require(priceCents >= 0) { "price must not be negative" }
    }

    // Readable from outside, changeable only from inside the class
    var priceCents: Long = priceCents
        private set

    fun applyDiscount(percent: Int) {
        require(percent in 0..100) { "percent must be 0..100" }
        priceCents -= priceCents * percent / 100
    }
}
