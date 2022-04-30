package it.polito.wa2.walletservice.dtos.wallet

import java.math.BigDecimal

interface WalletDTO{

    val id: Long?
    val amount: BigDecimal
    val owner: Long

}

//TODO: FORSE NON E' DA USARE
