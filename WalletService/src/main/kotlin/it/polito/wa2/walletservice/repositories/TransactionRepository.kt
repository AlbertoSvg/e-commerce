package it.polito.wa2.walletservice.repositories

import it.polito.wa2.walletservice.entities.Transaction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface TransactionRepository : PagingAndSortingRepository<Transaction, Long> {

    /** ### Description:
     * Find all transactions related to a particular wallet ID between a specific time interval
     * @param walletId The id of the wallet (Long)
     * @param from The date expressed in milliseconds where the interval begins (LocalDateTime)
     * @param to The date expressed in milliseconds where the interval ends (LocalDateTime)
     * @return A sublist of a list of Transaction.
     */
    @Query(
        value = "SELECT t " +
                "FROM Transaction t " +
                "WHERE t.walletReceiver.id = :walletId " +
                "AND t.timestamp >= :from " +
                "AND t.timestamp <= :to OR t.walletSender.id = :walletId " +
                "AND t.timestamp >= :from AND t.timestamp <= :to",
        countQuery = "SELECT COUNT(t) " +
                "FROM Transaction t " +
                "WHERE t.walletReceiver.id = :walletId " +
                "AND t.timestamp >= :from " +
                "AND t.timestamp <= :to OR t.walletSender.id = :walletId " +
                "AND t.timestamp >= :from AND t.timestamp <= :to"
    )
    fun findByWalletSenderOrWalletReceiverAndTimestampBetween(
        @Param("walletId") wallet: Long,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime,
        pageable: Pageable
    ): Page<Transaction>

    /** ### Description:
     * Find a Transaction using the ID number
     * @param id The id of the transaction (Long)
     * @return A container object (of Transaction) which may or may not contain a non-null value.
     */
    override fun findById(id: Long): Optional<Transaction>

}
