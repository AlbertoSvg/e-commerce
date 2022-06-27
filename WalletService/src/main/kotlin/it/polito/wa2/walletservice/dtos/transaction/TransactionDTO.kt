package it.polito.wa2.walletservice.dtos.transaction

import it.polito.wa2.walletservice.enum.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class TransactionDTO(

    val id: Long?,

    @field:Positive
    @field:NotNull
    val amount: BigDecimal,

    val sender: Long?,

    @field:NotNull val receiver: Long,

    val timestamp: LocalDateTime?,

    val type: TransactionType,

    val operationRef:String
)
