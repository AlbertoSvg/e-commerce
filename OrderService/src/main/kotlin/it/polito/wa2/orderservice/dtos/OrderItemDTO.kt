package it.polito.wa2.orderservice.dtos

import it.polito.wa2.orderservice.entities.OrderItem
import java.math.BigDecimal

data class OrderItemDTO(
    //val id: Long?,
    val productId: Long?,
    //val orderId: Long?,
    val amount: Long?,
    val price: BigDecimal?,
    val warehouseId: Long?,
) {
    fun toOrderItemEntity(): OrderItem {
        val orderItem = OrderItem().also {
            //it.orderId = this.orderId
            it.productId = this.productId
            it.warehouseId = this.warehouseId
            it.price = this.price
            it.amount = this.amount
        }
        return orderItem
    }
}

