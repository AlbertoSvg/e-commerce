package it.polito.wa2.orderservice.dtos.order.request

import it.polito.wa2.orderservice.constants.OrderStatus

data class UpdateOrderRequestDTO(
    val deliveryAddress: String?,
    val status: OrderStatus?
)