package m08_sealed_enums.shop

/*
 * Shop snapshot for module 08: order status as a sealed hierarchy.
 * Each state carries exactly the data that makes sense for it.
 */
sealed interface OrderStatus {
    data object Placed : OrderStatus
    data class Shipped(val trackingId: String) : OrderStatus
    data class Delivered(val signedBy: String) : OrderStatus
    data class Cancelled(val reason: String) : OrderStatus
}

fun OrderStatus.label(): String = when (this) {
    OrderStatus.Placed -> "Order placed"
    is OrderStatus.Shipped -> "On the way ($trackingId)"
    is OrderStatus.Delivered -> "Delivered, signed by $signedBy"
    is OrderStatus.Cancelled -> "Cancelled: $reason"
}
