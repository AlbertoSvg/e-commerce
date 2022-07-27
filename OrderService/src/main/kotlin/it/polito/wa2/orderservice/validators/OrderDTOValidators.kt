package it.polito.wa2.orderservice.validators

import it.polito.wa2.orderservice.dtos.OrderDTO


/**
 * All fields, id and status excluded, not null. Items list not empty
 */
fun OrderDTO.validatePost() : Boolean {
    return this.userId != null && this.walletId != null && this.deliveryAddress != null && this.items != null && this.items.isNotEmpty()
}

/**
 * At least one field not null
 */
fun OrderDTO.validatePatch() : Boolean {
    return this.userId != null && (this.walletId != null || this.deliveryAddress != null || this.status != null || (this.items != null && this.items.isNotEmpty()))
}

