package it.polito.wa2.orderservice.dtos.order.request

import it.polito.wa2.orderservice.entities.OrderItem
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class PurchaseProductDTO(
    @field:NotNull
    val productId: Long,
    @field:NotNull
    @field:Min(1)
    val amount: Long,
    @field:DecimalMin("0.00", inclusive= true)
    @field:Digits(fraction=2, integer = 10)
    @field:NotNull
    val price: BigDecimal
){
    fun toOrderItemEntity(): OrderItem {
        val orderItem = OrderItem().also {
            //it.orderId = this.orderId
            it.productId = this.productId
            it.warehouseId = null
            it.price = this.price
            it.amount = this.amount
        }
        return orderItem
    }
}