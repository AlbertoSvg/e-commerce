package it.polito.wa2.walletservice.entities

import it.polito.wa2.walletservice.dtos.TransactionDTO
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.*
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Positive


@Entity
@Table(name = "transaction")
class Transaction : EntityBase<Long>() {

    var id = getId()

    @Positive
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "999999999999")
    @Column(
        name = "amount",
        nullable = false,
        updatable = false,
        columnDefinition = "DECIMAL(12,2)"
    )
    var amount: BigDecimal? = null

    @Column(
        name = "timestamp",
        nullable = false,
        columnDefinition = "TIMESTAMP(3)",
        updatable = false
    )
    var timestamp: LocalDateTime? = null

    @PrePersist
    fun prePersistCreatedAt() {
        this.timestamp = LocalDateTime.now()
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "wallet_sender_id",
        referencedColumnName = "id",
        nullable = true,  // Nullable when a customer recharge himself
        updatable = false
    )
    lateinit var walletSender: Wallet

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "wallet_receiver_id",
        referencedColumnName = "id",
        nullable = false,
        updatable = false
    )
    lateinit var walletReceiver: Wallet

}

// Extension function
fun Transaction.toTransactionDTO(): TransactionDTO = TransactionDTO(
    id = id,
    timestamp = timestamp?.truncatedTo(ChronoUnit.MILLIS),
    amount = amount!!,
    sender = walletSender.id,
    receiver = walletReceiver.id!!
)
