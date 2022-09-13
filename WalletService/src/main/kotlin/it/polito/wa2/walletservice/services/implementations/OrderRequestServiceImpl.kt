package it.polito.wa2.walletservice.services.implementations

import it.polito.wa2.saga.costants.Topics.orderStatusTopic
import it.polito.wa2.saga.services.MessageService
import it.polito.wa2.saga.services.ProcessingLogService
import it.polito.wa2.saga.utils.parseID
import it.polito.wa2.walletservice.dtos.order.request.*
import it.polito.wa2.walletservice.entities.Transaction
import it.polito.wa2.walletservice.entities.WalletType
import it.polito.wa2.walletservice.enum.TransactionType
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import it.polito.wa2.walletservice.services.OrderRequestService
import it.polito.wa2.walletservice.services.WalletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
class OrderRequestServiceImpl : OrderRequestService {

    @Autowired
    lateinit var walletService: WalletService

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Autowired
    lateinit var walletRepository: WalletRepository

    @Autowired
    lateinit var processingLogService: ProcessingLogService

    @Autowired
    lateinit var messageService: MessageService

    @Autowired
    lateinit var self: OrderRequestService

    override fun process(orderRequestDTO: WalletOrderRequestDTO, id: String) {
        val uuid = UUID.fromString(id)
        if (processingLogService.isProcessed(uuid))
            return

        var status: OrderStatusDTO? = null
        try {
            status = self.processOrderRequest(orderRequestDTO)
        } catch (e: Exception) {
            println("Error during order processing!")
            status = OrderStatusDTO(
                orderRequestDTO.orderId,
                ResponseStatus.FAILED,
                e.message
            )
        } finally {
            processingLogService.process(uuid)
            status?.also {
                messageService.publish(
                    it,
                    if (it.responseStatus == ResponseStatus.COMPLETED)
                        EventTypeOrderStatus.OrderOk.toString()
                    else EventTypeOrderStatus.OrderPaymentFailed.toString(),
                    orderStatusTopic
                )
            }
        }
    }

    override fun processOrderRequest(orderRequestDTO: WalletOrderRequestDTO): OrderStatusDTO? {

        val orderId = orderRequestDTO.orderId
        val userWallet =
            walletRepository.findByIdAndOwnerAndWalletType(
                orderRequestDTO.walletFrom.parseID(),
                orderRequestDTO.userId.parseID(),
                WalletType.CUSTOMER
            )
                ?: return OrderStatusDTO(
                    orderId,
                    ResponseStatus.FAILED,
                    "Cannot find required wallet"
                )

        if (orderRequestDTO is WalletOrderPaymentDTO) {
            val ecommerceWallet = walletRepository.findByWalletTypeAndOwner(
                WalletType.ECOMMERCE,
                -1
            ) ?: throw RuntimeException("Cannot find ECOMMERCE wallet")

            val transaction = Transaction().also {
                it.walletSender = userWallet
                it.walletReceiver = ecommerceWallet
                it.type = TransactionType.ORDER_PAYMENT
                it.amount = orderRequestDTO.amount
                it.operationRef = orderId
            }

            walletService.orderTransaction(transaction)

            return OrderStatusDTO(
                orderId,
                ResponseStatus.COMPLETED,
                null
            )
        } else if (orderRequestDTO is WalletOrderRefundDTO) {
            //REFUND
            val previousTransactions =
                transactionRepository.findByWalletSenderAndOperationRefAndType(userWallet, orderId)
            for (previousTransaction in previousTransactions) {

                val transaction = Transaction().also {
                    it.walletSender =
                        walletService.getWalletById(previousTransaction.walletReceiver.getId()!!, null, null, false)
                    it.walletReceiver = userWallet
                    it.type = TransactionType.ORDER_REFUND
                    it.amount = previousTransaction.amount
                    it.operationRef = orderId
                }


                walletService.orderTransaction(transaction)

            }

        }
        return null
    }

}