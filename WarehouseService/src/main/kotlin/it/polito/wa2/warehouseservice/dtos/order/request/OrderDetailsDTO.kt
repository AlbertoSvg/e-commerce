package it.polito.wa2.warehouseservice.dtos.order.request

import it.polito.wa2.saga.entities.Emittable
import javax.validation.constraints.NotNull

// message sent from warehouse after updating warehouses stocks

class OrderDetailsDTO(
    @field:NotNull
    val orderId: String,
    @field:NotNull
    val productWarehouseList: List<ProductWarehouseDTO>
): Emittable {
    override fun getId(): String {
        return orderId
    }
}