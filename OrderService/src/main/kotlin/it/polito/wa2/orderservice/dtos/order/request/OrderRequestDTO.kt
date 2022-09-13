package it.polito.wa2.orderservice.dtos.order.request

import it.polito.wa2.orderservice.constants.OrderStatus
import it.polito.wa2.orderservice.dtos.OrderItemDTO
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.Size

data class OrderRequestDTO(
    val userId: Long,
    val walletId: Long,
    val deliveryAddress: String,
    val items: List<PurchaseProductDTO>
)