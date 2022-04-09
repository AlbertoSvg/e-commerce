package it.polito.wa2.walletservice.entities

import it.polito.wa2.walletservice.dtos.wallet.CustomerWalletDTO
import it.polito.wa2.walletservice.dtos.wallet.WalletDTO
import it.polito.wa2.walletservice.dtos.wallet.WarehouseWalletDTO
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull
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

    //TODO: come gestire il customer che è in possesso del wallet visto che i customer(user) sono gestiti nel servizio CatalogService
/*
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    lateinit var customer: Customer

 */

    @Column(nullable = false)
    var owner: Long = -1

    @Column(nullable = false)
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

    fun addPurchaseTransaction(t: Transaction) {
        t.walletSender = this
        purchases.add(t)
    }

    fun addRechargeTransaction(t: Transaction) {
        t.walletReceiver = this
        recharges.add(t)
    }

}

// Extension function
fun Wallet.toWalletDTO(): WalletDTO {
    return when(walletType) {
        WalletType.WAREHOUSE-> WarehouseWalletDTO(getId(), amount, owner)
        WalletType.CUSTOMER-> CustomerWalletDTO(getId(), amount, owner)
    }
}

enum class WalletType{
    CUSTOMER, WAREHOUSE
}
