package it.polito.wa2.catalogservice.dtos

import java.math.BigDecimal

data class WalletDTO(

    val id: Long?,
    val amount: BigDecimal,
    val owner: Long,
    val type: String

)
