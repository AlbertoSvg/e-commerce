package it.polito.wa2.warehouseservice.validators

import it.polito.wa2.warehouseservice.dtos.ProductDTO

//TODO: se/quando aggiungiamo membri al productDTO, aggiungerli ai validator

/**
 * All fields, id excluded, not null
 */
fun ProductDTO.validatePost() : Boolean {
    return this.category != null && this.description != null && this.price != null
}

/**
 * All fields, id excluded, not null
 */
fun ProductDTO.validatePut() : Boolean {
    return this.category != null && this.description != null && this.price != null
}

/**
 * At least one field not null
 */
fun ProductDTO.validatePatch() : Boolean {
    return this.category != null || this.description != null || this.price != null
}