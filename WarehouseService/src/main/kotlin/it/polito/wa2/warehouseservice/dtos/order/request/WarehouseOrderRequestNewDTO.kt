package it.polito.wa2.warehouseservice.dtos.order.request

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class WarehouseOrderRequestNewDTO(
    @field:NotNull
    override val orderId: String,
    @field:NotNull
    val buyerId: String,
    @field:NotNull
    val buyerWalletId: String,
    @field:NotNull @field:Size(min = 1)
    val productList: List<PurchaseProductDTO>

) : WarehouseOrderRequestDTO {
    override val requestType = RequestType.NEW
}