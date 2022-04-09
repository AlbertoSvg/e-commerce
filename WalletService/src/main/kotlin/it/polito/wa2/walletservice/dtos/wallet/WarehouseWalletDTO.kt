package it.polito.wa2.walletservice.dtos.wallet

import java.math.BigDecimal

class WarehouseWalletDTO(
    override val id: Long?,
    override val amount: BigDecimal,
    val warehouseId: Long
): WalletDTO {
}
