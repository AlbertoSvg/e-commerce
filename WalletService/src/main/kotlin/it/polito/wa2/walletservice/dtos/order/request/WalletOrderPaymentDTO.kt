package it.polito.wa2.walletservice.dtos.order.request

import it.polito.wa2.walletservice.dtos.transaction.request.OrderTransactionDTO
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class WalletOrderPaymentDTO(
    @field:NotNull(message = "WalletFrom must be present")
    override val walletFrom: String,
    @field:NotNull(message = "UserID must be present")
    override val userId: String,
    @field:NotNull(message = "OrderID must be present")
    override val orderId: String,
    @field:NotNull(message = "List of transactions must be present") @field:Size(min=1)
    val transactionList:List<OrderTransactionDTO>
): WalletOrderRequestDTO {

    override var requestType: OrderPaymentType = OrderPaymentType.PAY
}
