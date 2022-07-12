package it.polito.wa2.warehouseservice.dtos.order.request

import javax.validation.constraints.NotNull

class WalletOrderRefundDTO (
    @field:NotNull(message = "WalletFrom must be present")
    override val walletFrom: String,
    @field:NotNull(message = "UserID must be present")
    override val userId: String,
    @field:NotNull(message = "OrderID must be present")
    override val orderId: String
): WalletOrderRequestDTO {

    override val requestType: OrderPaymentType = OrderPaymentType.REFUND
}