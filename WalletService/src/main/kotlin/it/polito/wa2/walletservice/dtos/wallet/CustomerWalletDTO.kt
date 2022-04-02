package it.polito.wa2.walletservice.dtos.wallet

import java.math.BigDecimal

class CustomerWalletDTO(
    override val id: Long?,
    override val amount: BigDecimal,
    val userId: Long
) : WalletDTO{
}
