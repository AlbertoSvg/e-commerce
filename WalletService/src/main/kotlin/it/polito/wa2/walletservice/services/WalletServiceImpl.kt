package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.costants.Strings.DESTINATION_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.INSUFFICIENT_CREDIT
import it.polito.wa2.walletservice.costants.Strings.SENDER_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.TRANSACTION_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.UNAUTHORIZED_USER
import it.polito.wa2.walletservice.costants.Strings.WALLET_NOT_FOUND
import it.polito.wa2.walletservice.dtos.transaction.TransactionDTO
import it.polito.wa2.walletservice.dtos.wallet.WalletDTO
import it.polito.wa2.walletservice.entities.*
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import it.polito.wa2.walletservice.utils.Utils
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

    @Autowired
    private lateinit var utils: Utils

    override fun addWalletToCustomer(customerId: Long, userId: String?, roles: String?): WalletDTO {
        if (!utils.isAuthorized(roles, userId, customerId))
            throw RuntimeException(UNAUTHORIZED_USER)
        val wallet = Wallet().also {
            it.amount = BigDecimal(0)
            it.owner = customerId
            it.walletType = WalletType.CUSTOMER
        }
        return walletRepository.save(wallet).toWalletDTO()
    }

    override fun getWalletById(walletId: Long, userId: String?, roles: String?): WalletDTO {
        val walletOpt = walletRepository.findById(walletId)
        if (walletOpt.isPresent) {
            if (utils.isAuthorized(roles, userId, walletOpt.get().owner))
                return walletOpt.get().toWalletDTO()
            else
                throw RuntimeException(UNAUTHORIZED_USER)
        }
        else
            throw RuntimeException(WALLET_NOT_FOUND)
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
