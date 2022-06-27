package it.polito.wa2.warehouseservice.dtos

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class AddCommentDTO(
    @field:NotNull
    val title: String,
    @field:NotNull
    val body: String,
    @field:NotNull @field:Min(0) @field:Max(5)
    val stars: Int,
    @field:NotNull
    val productId: Long
)