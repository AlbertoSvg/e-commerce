package it.polito.wa2.warehouseservice.dtos.order.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import it.polito.wa2.saga.entities.Emittable


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "requestType")
@JsonSubTypes(
    value =  [
        JsonSubTypes.Type(value = WalletOrderPaymentDTO::class,  name = "PAY"),
        JsonSubTypes.Type(value = WalletOrderRefundDTO::class,  name = "REFUND")
    ])
interface WalletOrderRequestDTO: Emittable {
    val walletFrom: String
    val userId: String
    val orderId: String
    val requestType: OrderPaymentType

    override fun getId(): String {
        return orderId
    }
}

enum class OrderPaymentType{
    PAY,
    REFUND
}