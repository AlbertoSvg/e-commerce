package it.polito.wa2.warehouseservice.validators

import it.polito.wa2.warehouseservice.dtos.ProductDTO
import java.math.BigDecimal

//TODO: se/quando aggiungiamo membri al productDTO, aggiungerli ai validator

/**
 * All fields, id excluded, not null
 */
fun ProductDTO.validatePost() : Boolean {
    return this.name != null && this.category != null && this.description != null && this.price != null && this.price > BigDecimal("0.00")
}

/**
 * All fields, id excluded, not null
 */
fun ProductDTO.validatePut() : Boolean {
    return this.name != null && this.category != null && this.description != null && this.price != null && this.price > BigDecimal("0.00")
}

/**
 * At least one field not null
 */
fun ProductDTO.validatePatch() : Boolean {
    return this.name != null || this.category != null || this.description != null || (this.price != null && this.price > BigDecimal("0.00"))
}