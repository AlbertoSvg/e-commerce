package it.polito.wa2.warehouseservice.dtos.order.request

import javax.validation.constraints.NotNull

data class ProductWarehouseDTO(
    @field:NotNull
    val warehouseId: Long,
    @field:NotNull
    val productId: Long
)