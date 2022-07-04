package it.polito.wa2.walletservice.entities

import it.polito.wa2.walletservice.dtos.wallet.WalletDTO
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.PositiveOrZero

@Entity
@Table(name = "wallet")
class Wallet : EntityBase<Long>() {

    var id = getId()

    @PositiveOrZero
    @Column(
        name = "amount",
        nullable = false,
        updatable = true
    )
    var amount: BigDecimal = BigDecimal("0.00")

    @Column(name = "owner", nullable = false)
    var owner: Long = -1

    @Column(name = "type", nullable = false)
    lateinit var walletType: WalletType

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "walletSender",
        targetEntity = Transaction::class,
    )
    var purchases = mutableSetOf<Transaction>()

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "walletReceiver",
        targetEntity = Transaction::class,
    )
    var recharges = mutableSetOf<Transaction>()

}

// Extension function
fun Wallet.toWalletDTO(): WalletDTO {
    return WalletDTO(id, amount, owner, walletType.name)
}

enum class WalletType{
    CUSTOMER, ECOMMERCE
}