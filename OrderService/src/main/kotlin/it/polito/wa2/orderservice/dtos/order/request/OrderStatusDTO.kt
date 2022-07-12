package it.polito.wa2.orderservice.dtos.order.request

import it.polito.wa2.saga.entities.Emittable
import org.jetbrains.annotations.NotNull

data class OrderStatusDTO(
    @field:NotNull
    val orderID: String,
    @field:NotNull
    val responseStatus: ResponseStatus,
    val errorMessage: String?
): Emittable {
    override fun getId(): String {
        return orderID
    }
}

enum class ResponseStatus{
    COMPLETED, // The transaction has been correctly executed
    FAILED // The transaction was not correctly executed
}

enum class EventTypeOrderStatus{
    OrderOk,
    OrderPaymentFailed,
    OrderItemsNotAvailable
}