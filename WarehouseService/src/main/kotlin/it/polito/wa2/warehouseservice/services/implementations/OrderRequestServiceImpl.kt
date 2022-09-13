package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.saga.costants.Topics.orderDetailsTopic
import it.polito.wa2.saga.costants.Topics.orderStatusTopic
import it.polito.wa2.saga.costants.Topics.paymentTopic
import it.polito.wa2.saga.services.MessageService
import it.polito.wa2.saga.services.ProcessingLogService
import it.polito.wa2.warehouseservice.dtos.order.request.*
import it.polito.wa2.warehouseservice.services.interfaces.OrderRequestService
import it.polito.wa2.warehouseservice.services.interfaces.ProductService
import it.polito.wa2.warehouseservice.services.interfaces.WarehouseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
@Transactional
class OrderRequestServiceImpl : OrderRequestService {

    @Autowired
    lateinit var warehouseService: WarehouseService

    @Autowired
    lateinit var productService: ProductService

    @Autowired
    lateinit var processingLogService: ProcessingLogService

    @Autowired
    lateinit var messageService: MessageService

    override fun process(orderRequestDTO: WarehouseOrderRequestDTO, id: String) {
        val uuid = UUID.fromString(id)
        if(processingLogService.isProcessed(uuid))
            return

        var orderDetails: OrderDetailsDTO? = null
        var paymentRequest: WalletOrderRequestDTO? = null
        var orderStatusDTO: OrderStatusDTO? = null

        try {

            if (orderRequestDTO is WarehouseOrderRequestNewDTO) {
                println("WarehouseOrderRequestNewDTO")
                // List<ProductWarehouseDTO>
                val productsWarehouseDTO = warehouseService.getWarehouseHavingProducts(orderRequestDTO.productList)
                // if it not throws exception, continue
                val amount = warehouseService.updateQuantityAndRetrieveAmount(orderRequestDTO.productList)
                orderDetails = OrderDetailsDTO(orderRequestDTO.orderId, productsWarehouseDTO)
                paymentRequest = WalletOrderPaymentDTO(
                    orderRequestDTO.buyerWalletId,
                    orderRequestDTO.buyerId,
                    orderRequestDTO.orderId,
                    amount
                )
            } else if (orderRequestDTO is WarehouseOrderRequestCancelDTO) {
                println("WarehouseOrderRequestCancelDTO")
                warehouseService.cancelRequestUpdate(orderRequestDTO.productList)

            }

        }
        catch (e:Exception){
            println("Error during order processing")
            orderStatusDTO = OrderStatusDTO(
                orderRequestDTO.orderId,
                ResponseStatus.FAILED,
                e.message)
        }
        finally {
            processingLogService.process(uuid)

            orderStatusDTO?.also {
                messageService.publish(it,
                    EventTypeOrderStatus.OrderItemsNotAvailable.toString(),
                    orderStatusTopic)
            }

            orderDetails?.also {
                messageService.publish(it,
                    "OrderDetails",
                    orderDetailsTopic)
            }

            paymentRequest?.also {
                messageService.publish(it,
                    "PaymentRequest",
                    paymentTopic)
            }

        }
    }
}