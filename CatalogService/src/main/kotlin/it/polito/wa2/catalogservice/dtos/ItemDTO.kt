package it.polito.wa2.catalogservice.dtos

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class ItemDTO (
    @field:NotNull
    val productId: Long,
    @field:NotNull
    @field:Min(1)
    val amount: Long
    )