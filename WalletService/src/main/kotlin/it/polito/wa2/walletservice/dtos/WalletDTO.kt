package it.polito.wa2.walletservice.dtos

import java.math.BigDecimal

data class WalletDTO(

    val id: Long?,
    val amount: BigDecimal,
    //val owner: Long TODO: da gestire

)
