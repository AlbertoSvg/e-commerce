package it.polito.wa2.warehouseservice.dtos.order.request

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class PurchaseProductDTO(
    @field:NotNull
    val productId: Long,
    @field:NotNull
    @field:Min(1)
    val amount: Int
)