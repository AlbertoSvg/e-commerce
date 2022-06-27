package it.polito.wa2.walletservice.dtos.transaction.request

import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.NotNull

data class RechargeTransactionDTO(
    @field:NotNull(message = "The amount MUST be present")
    @field:Digits(fraction = 2, integer = 10)
    @field:DecimalMin(value = "0.00", inclusive = true, message = "The amount should NOT be negative")
    val amount: BigDecimal
)
