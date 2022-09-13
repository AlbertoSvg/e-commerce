package it.polito.wa2.orderservice.dtos.order.request


import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class PurchasedProductDTO (
        @field:NotNull
        val productId: Long,
        @field:NotNull
        @field:Min(1)
        val amount: Long
    )