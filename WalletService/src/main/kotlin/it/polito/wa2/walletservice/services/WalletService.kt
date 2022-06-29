package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.dtos.transaction.TransactionDTO
import it.polito.wa2.walletservice.dtos.transaction.request.RechargeTransactionDTO
import it.polito.wa2.walletservice.dtos.wallet.WalletDTO
import it.polito.wa2.walletservice.entities.Transaction
import it.polito.wa2.walletservice.entities.Wallet
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.LocalDateTime


interface WalletService {

    /** ### Description:
     * Add a wallet to a Customer by customerID (Long)
     * @param customerId The id of the customer (Long)
     * @return A WalletDTO.
     */
    fun addWalletToCustomer(customerId: Long): WalletDTO

    /** ### Description:
     * Get a wallet using the wallet ID (Long)
     * @param walletId The id of the wallet (Long)
     * @return A Wallet.
     */
    fun getWalletById(walletId: Long, userId: String?, roles: String?, checkAuthorization: Boolean = true): Wallet


    /** ### Description:
     * Perform a transaction moving money between two wallets
     * @param walletSource The ID of the wallet that is sending the money (Long)
     * @param walletDest The ID of the wallet that is receiving the money (Long)
     * @param amount The total amount of money that will be transferred  (BigDecimal)
     * @return A TransactionDTO.
     */
    fun orderTransaction(transaction: Transaction): Transaction

    /** ### Description:
     * Get all the wallet's transactions in a given date range by walletID (Long)
     * @param walletId the ID of the wallet whose transactions are to be searched for in a given time frame (Long)
     * @param from The date expressed in milliseconds where the interval begins (LocalDateTime)
     * @param to The date expressed in milliseconds where the interval ends (LocalDateTime)
     * @param size The number of records per page
     * @param pageNo The number of the requested page
     * @return A sublist of a list of TransactionDTO divided by pagination.
     */
    fun getTransactionsByDateRange(
        walletId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        pageNo: Int,
        size: Int
    ): Page<TransactionDTO>

    /** ### Description:
     * Get a single transaction using a walletID (Long)
     * @param walletId the ID of the wallet (Long)
     * @param transactionId The transaction ID (Long)
     * @return A TransactionDTO.
     */
    fun getTransaction(walletId: Long, transactionId: Long): TransactionDTO

    fun rechargeTransaction(receiverWalletId: Long, rechargeTransaction: RechargeTransactionDTO) : TransactionDTO

}
