package it.polito.wa2.warehouseservice.dtos.order.request

import java.math.BigDecimal
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

class WalletOrderPaymentDTO(
    @field:NotNull(message = "WalletFrom must be present")
    override val walletFrom: String,
    @field:NotNull(message = "UserID must be present")
    override val userId: String,
    @field:NotNull(message = "OrderID must be present")
    override val orderId: String,
    @field:NotNull(message = "Transaction amount must be present")
    @field:Positive(message = "Transaction amount must be positive")
    val amount: BigDecimal
): WalletOrderRequestDTO {

    override var requestType: OrderPaymentType = OrderPaymentType.PAY
}