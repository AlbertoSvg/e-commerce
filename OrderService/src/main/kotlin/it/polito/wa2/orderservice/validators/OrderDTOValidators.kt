package it.polito.wa2.orderservice.validators

import it.polito.wa2.orderservice.dtos.OrderDTO
import it.polito.wa2.orderservice.dtos.order.request.OrderRequestDTO
import it.polito.wa2.orderservice.dtos.order.request.UpdateOrderRequestDTO


/**
 * All fields, id and status excluded, not null. Items list not empty
 */
fun OrderRequestDTO.validatePost() : Boolean {
    return this.items.isNotEmpty()
}

/**
 * At least one field not null
 */
fun UpdateOrderRequestDTO.validatePatch() : Boolean {
    return this.status != null || this.deliveryAddress != null
}

