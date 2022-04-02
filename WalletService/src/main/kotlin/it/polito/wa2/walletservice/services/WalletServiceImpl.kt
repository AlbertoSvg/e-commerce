package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.costants.Strings.DESTINATION_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.INSUFFICIENT_CREDIT
import it.polito.wa2.walletservice.costants.Strings.SENDER_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.TRANSACTION_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.WALLET_NOT_FOUND
import it.polito.wa2.walletservice.dtos.transaction.TransactionDTO
import it.polito.wa2.walletservice.dtos.wallet.WalletDTO
import it.polito.wa2.walletservice.entities.Transaction
import it.polito.wa2.walletservice.entities.Wallet
import it.polito.wa2.walletservice.entities.toTransactionDTO
import it.polito.wa2.walletservice.entities.toWalletDTO
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime


@Service
@Transactional
class WalletServiceImpl() : WalletService {
    //TODO: da gestire
    /*@Autowired
    private lateinit var customerRepository: CustomerRepository
*/
    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    override fun addWalletToCustomer(customerId: Long): WalletDTO {
        val wallet = Wallet()
        //TODO: da gestire
        /*val username = SecurityContextHolder.getContext().authentication.name
        val optCustomer = customerRepository.findById(customerId)
        if (optCustomer.isEmpty) throw RuntimeException(CUSTOMER_NOT_FOUND)
        val customer = optCustomer.get()
        if (customer.user.username != username) throw RuntimeException(Strings.CUSTOMER_NOT_MATCHING)
        customer.addWallet(wallet)

         */
        return walletRepository.save(wallet).toWalletDTO()
    }

    override fun getWalletById(walletId: Long): WalletDTO {
        val wallet = walletRepository.findById(walletId)
        if (wallet.isPresent) throw RuntimeException(WALLET_NOT_FOUND)
        return wallet.get().toWalletDTO()
    }

    override fun executeTransaction(walletSource: Long, walletDest: Long, amount: BigDecimal): TransactionDTO {
        val username = SecurityContextHolder.getContext().authentication.name
        val senderOptional = walletRepository.findById(walletSource)
        val destinationOptional = walletRepository.findById(walletDest)
        if (senderOptional.isPresent) throw RuntimeException(SENDER_WALLET_NOT_FOUND)
        if (destinationOptional.isPresent) throw RuntimeException(DESTINATION_WALLET_NOT_FOUND)

        val senderWallet = senderOptional.get()
        val destinationWallet = destinationOptional.get()

        //TODO: da gestire
        //if (senderWallet.customer.user.username != username) throw RuntimeException(Strings.WALLET_DOES_NOT_BELONG)

        if (amount > senderWallet.amount) throw RuntimeException(INSUFFICIENT_CREDIT)

        senderWallet.amount -= amount
        destinationWallet.amount += amount

        val transaction = Transaction().also {
            it.amount = amount
            it.walletSender = senderWallet
            it.walletReceiver = destinationWallet
        }

        // Update the amount of senderWallet
        walletRepository.save(senderWallet)
        // Update the amount of destinationWallet
        walletRepository.save(destinationWallet)

        return transactionRepository.save(transaction).toTransactionDTO()
    }

    override fun getTransaction(walletId: Long, transactionId: Long): TransactionDTO {
        val transactionOptional = transactionRepository.findById(transactionId)
        if (transactionOptional.isPresent) throw RuntimeException(TRANSACTION_NOT_FOUND)
        val transaction = transactionOptional.get()
        if (transaction.walletSender.id != walletId && transaction.walletReceiver.id != walletId)
            throw RuntimeException(WALLET_NOT_FOUND)
        return transaction.toTransactionDTO()
    }


    override fun getTransactionsByDateRange(
        walletId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
        pageNo: Int,
        size: Int
    ): Page<TransactionDTO> {
        val paging = PageRequest.of(pageNo, size)
        if (walletRepository.findById(walletId).isPresent) throw RuntimeException(WALLET_NOT_FOUND)
        val transactions =
            transactionRepository.findByWalletSenderOrWalletReceiverAndTimestampBetween(walletId, from, to, paging)
        if (transactions.isEmpty) throw  RuntimeException(TRANSACTION_NOT_FOUND)
        return transactions.map { it.toTransactionDTO() }
    }

}
