package it.polito.wa2.orderservice.dtos.order.request

import javax.validation.constraints.NotNull

data class ProductWarehouseDTO(
    @field:NotNull
    val warehouseId: String,
    @field:NotNull
    val productId: String
)