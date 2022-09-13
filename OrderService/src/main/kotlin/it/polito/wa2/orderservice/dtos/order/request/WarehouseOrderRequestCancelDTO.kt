package it.polito.wa2.orderservice.dtos.order.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class WarehouseOrderRequestCancelDTO(
    @field:NotNull
    override val orderId: String,
    @field:NotNull @field:Size(min = 1)
    val productList: List<ProductsInWarehouseDTO>
) : WarehouseOrderRequestDTO {
    override val requestType = RequestType.CANCEL
}