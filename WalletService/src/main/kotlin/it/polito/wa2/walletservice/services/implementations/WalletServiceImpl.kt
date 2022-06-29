package it.polito.wa2.walletservice.services.implementations

import it.polito.wa2.saga.utils.parseID
import it.polito.wa2.walletservice.costants.Strings.DESTINATION_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.INSUFFICIENT_CREDIT
import it.polito.wa2.walletservice.costants.Strings.INVALID_TRANSACTION
import it.polito.wa2.walletservice.costants.Strings.ORDER_PAYMENT_FAILED
import it.polito.wa2.walletservice.costants.Strings.OUT_OF_MONEY
import it.polito.wa2.walletservice.costants.Strings.SENDER_WALLET_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.TRANSACTION_NOT_FOUND
import it.polito.wa2.walletservice.costants.Strings.TRANSACTION_NOT_FOUND_FOR_WALLET
import it.polito.wa2.walletservice.costants.Strings.UNAUTHORIZED_USER
import it.polito.wa2.walletservice.costants.Strings.WALLET_NOT_FOUND
import it.polito.wa2.walletservice.dtos.transaction.TransactionDTO
import it.polito.wa2.walletservice.dtos.transaction.request.RechargeTransactionDTO
import it.polito.wa2.walletservice.dtos.wallet.WalletDTO
import it.polito.wa2.walletservice.entities.*
import it.polito.wa2.walletservice.enum.TransactionType
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import it.polito.wa2.walletservice.services.WalletService
import it.polito.wa2.walletservice.utils.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Service
@Transactional
class WalletServiceImpl() : WalletService {

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var utils: Utils

    override fun addWalletToCustomer(
        customerId: Long
    ): WalletDTO {
        val wallet = Wallet().also {
            it.walletType = WalletType.CUSTOMER
            it.amount = BigDecimal(0)
            it.owner = customerId
        }
        return walletRepository.save(wallet).toWalletDTO()
    }

    override fun getWalletById(
        walletId: Long,
        userId: String?,
        roles: String?,
        checkAuthorization: Boolean
    ): Wallet {
        val walletOpt = walletRepository.findById(walletId)
        if (walletOpt.isPresent) {
            if (!checkAuthorization || utils.isAuthorized(roles, userId, walletOpt.get().owner))
                return walletOpt.get()
            else
                throw RuntimeException(UNAUTHORIZED_USER)
        }
        else {
            throw RuntimeException(WALLET_NOT_FOUND)
        }
    }

    //ADMINS ONLY
    override fun rechargeTransaction(receiverWalletId: Long, rechargeTransaction: RechargeTransactionDTO) : TransactionDTO {
        val destinationOptional = walletRepository.findById(receiverWalletId)
        if (!destinationOptional.isPresent) throw RuntimeException(DESTINATION_WALLET_NOT_FOUND)
        val destinationWallet = destinationOptional.get()
        if(rechargeTransaction.amount <= BigDecimal("0")) throw RuntimeException(INVALID_TRANSACTION)
        destinationWallet.amount += rechargeTransaction.amount
        val transaction = Transaction().also {
            it.amount = rechargeTransaction.amount
            it.walletSender = destinationWallet
            it.walletReceiver = destinationWallet
            it.type = TransactionType.RECHARGE
            it.operationRef = UUID.randomUUID().toString()
        }

        //destinationWallet.addRechargeTransaction(transaction) //TODO: DA TESTARE (se funziona aggiungerlo ovunque venga fatta una transazione)

        // Update the amount of destinationWallet
        walletRepository.save(destinationWallet)

        return transactionRepository.save(transaction).toTransactionDTO()
    }


    override fun orderTransaction(transaction: Transaction, checkAuthorization: Boolean): Transaction {
        val amount = transaction.amount

        when(transaction.type) {
            TransactionType.ORDER_PAYMENT -> {
                if(transaction.walletSender.amount < amount){
                    throw RuntimeException(OUT_OF_MONEY)
                }
                if(transaction.walletReceiver.walletType != WalletType.ECOMMERCE){
                    throw RuntimeException(ORDER_PAYMENT_FAILED)
                }

                transaction.walletSender.amount = transaction.walletSender.amount.minus(amount!!)
                transaction.walletReceiver.amount = transaction.walletReceiver.amount.plus(amount)
            }

            TransactionType.ORDER_REFUND -> {
                if (transaction.walletSender.walletType != WalletType.ECOMMERCE){
                    throw RuntimeException(ORDER_PAYMENT_FAILED)
                }
                transaction.walletSender.amount = transaction.walletSender.amount.minus(amount!!)
                transaction.walletReceiver.amount = transaction.walletReceiver.amount.plus(amount)
            }
            else -> {
                throw RuntimeException("This should never happen")
            }
        }

        transaction.walletSender.apply {
            walletRepository.save(this)
        }
        walletRepository.save(transaction.walletReceiver)
        return transactionRepository.save(transaction)

    }

    override fun getTransaction(walletId: Long, transactionId: Long): TransactionDTO {

        val transactionOptional = transactionRepository.findById(transactionId)
        if (!transactionOptional.isPresent) throw RuntimeException(TRANSACTION_NOT_FOUND)
        val transaction = transactionOptional.get()
//        if(!utils.isAuthorized(roles, userId, transaction.walletSender.owner) && !utils.isAuthorized(roles, userId, transaction.walletReceiver.owner) )
//            throw RuntimeException(UNAUTHORIZED_USER)
        if (transaction.walletSender.id != walletId && transaction.walletReceiver.id != walletId)
            throw RuntimeException(TRANSACTION_NOT_FOUND_FOR_WALLET)

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
        val walletOptional = walletRepository.findById(walletId)
        if (!walletOptional.isPresent) throw RuntimeException(WALLET_NOT_FOUND)
//        if(!utils.isAuthorized(roles,userId,walletOptional.get().owner))
//            throw RuntimeException(UNAUTHORIZED_USER)
        val transactions =
            transactionRepository.findByWalletSenderOrWalletReceiverAndTimestampBetween(walletId, from, to, paging)
        if (transactions.isEmpty) throw  RuntimeException(TRANSACTION_NOT_FOUND)
        return transactions.map { it.toTransactionDTO() }
    }

}
