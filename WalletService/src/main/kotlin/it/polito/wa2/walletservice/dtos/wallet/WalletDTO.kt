package it.polito.wa2.walletservice.dtos.wallet

import it.polito.wa2.walletservice.entities.WalletType
import java.math.BigDecimal

data class WalletDTO(

    val id: Long?,
    val amount: BigDecimal,
    val owner: Long,
    val type: WalletType

)

//TODO: FORSE NON E' DA USARE
