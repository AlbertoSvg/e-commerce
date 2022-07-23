package it.polito.wa2.orderservice.dtos

import it.polito.wa2.orderservice.constants.OrderStatus

data class OrderDTO (
    val id: Long?,
    val userId: Long?,
    val walletId: Long?,
    val deliveryAddress: String?,
    val status: OrderStatus?,
    val items: List<OrderItemDTO>?
)