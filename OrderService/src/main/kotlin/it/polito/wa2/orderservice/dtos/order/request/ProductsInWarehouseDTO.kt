package it.polito.wa2.orderservice.dtos.order.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class ProductsInWarehouseDTO(
    @field:NotNull
    val warehouseId: Long?,
    @field:NotNull @field:Size(min = 1)
    val purchasedProducts: List<PurchasedProductDTO>
)