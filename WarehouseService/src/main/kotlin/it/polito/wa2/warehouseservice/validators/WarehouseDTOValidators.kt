package it.polito.wa2.warehouseservice.validators

import it.polito.wa2.warehouseservice.dtos.WarehouseDTO

//TODO: se/quando aggiungiamo membri al warehouseDTO, aggiungerli ai validator

/**
 * All fields, id excluded, not null
 */
fun WarehouseDTO.validatePost() : Boolean {
    return this.name != null
}

/**
 * All fields not null
 */
fun WarehouseDTO.validatePut() : Boolean {
    return this.name != null
}

/**
 * At least one field not null
 */
fun WarehouseDTO.validatePatch() : Boolean {
    return this.name != null
}