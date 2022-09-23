package it.polito.wa2.orderservice.services.implementations

import it.polito.wa2.orderservice.constants.OrderStatus
import it.polito.wa2.orderservice.constants.Values
import it.polito.wa2.orderservice.constants.Values.ORDER_NOT_CANCELABLE
import it.polito.wa2.orderservice.constants.Values.ORDER_NOT_FOUND
import it.polito.wa2.orderservice.constants.Values.ORDER_NOT_UPDATABLE
import it.polito.wa2.orderservice.constants.Values.UNAUTHORIZED
import it.polito.wa2.orderservice.dtos.OrderCheckDTO
import it.polito.wa2.orderservice.dtos.OrderDTO
import it.polito.wa2.orderservice.dtos.order.request.*
import it.polito.wa2.orderservice.entities.Order
import it.polito.wa2.orderservice.repositories.OrderItemRepository
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.orderservice.services.interfaces.OrderService
import it.polito.wa2.orderservice.utils.extractProductInWarehouse
import it.polito.wa2.saga.costants.Topics.mailTopic
import it.polito.wa2.saga.costants.Topics.orderRequestTopic
import it.polito.wa2.saga.dtos.MailDTO
import it.polito.wa2.saga.services.MessageService
import it.polito.wa2.saga.services.ProcessingLogService
import it.polito.wa2.saga.utils.parseID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class OrderServiceImpl: OrderService {

    @Autowired
    lateinit var processingLogService: ProcessingLogService

    @Autowired
    lateinit var messageService: MessageService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderItemRepository: OrderItemRepository

    override fun getOrders(pageNo: Int, pageSize: Int): Page<OrderDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        val orders: Page<Order> = orderRepository.findAll(paging)
        return orders.map { order -> order.toOrderDTO() }
    }

    override fun getCustomerOrders(userId: String, pageNo: Int, pageSize: Int): Page<OrderDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        val orders: Page<Order> = orderRepository.findAllByUserId(userId.toLong(), paging)
        return orders.map { order -> order.toOrderDTO() }
    }

    override fun getOrderById(orderId: Long): OrderDTO {
        val order = orderRepository.findById(orderId)
        if (order.isEmpty) throw RuntimeException(Values.ORDER_NOT_FOUND)
        return order.get().toOrderDTO()
    }

    override fun checkPurchase(productId: Long, userId: Long): OrderCheckDTO {
        val orders = orderRepository.findAllByUserId(userId)
        val order = orders.find { order: Order ->
            order.items.any { orderItem -> orderItem.productId == productId }
        }
        return if (order == null)
            OrderCheckDTO(false)
        else
            OrderCheckDTO(true)
    }

    override fun createOrder(orderDTO: OrderRequestDTO): OrderDTO {
        var order = Order().also {
            it.userId = orderDTO.userId
            it.walletId = orderDTO.walletId
            it.deliveryAddress = orderDTO.deliveryAddress
            it.orderStatus = OrderStatus.PENDING
        }
        order = orderRepository.save(order)
        orderDTO.items.forEach { item ->
            val orderItem = item.toOrderItemEntity()
            orderItem.order = order
            order.items.add(orderItem)
        }
        orderItemRepository.saveAll(order.items)

        val orderMessage = WarehouseOrderRequestNewDTO(
            order.id.toString(),
            order.userId.toString(),
            order.walletId.toString(),
            order.items.map { it.toPurchaseProductDTO() }
        )

        messageService.publish(orderMessage, "NewOrder", orderRequestTopic)
        return order.toOrderDTO()
    }

    override fun updateOrder(userId: String, orderId: Long, updateOrderRequestDTO: UpdateOrderRequestDTO, authorized: Boolean): OrderDTO {
        val orderOpt = orderRepository.findById(orderId)
        if (orderOpt.isEmpty) throw RuntimeException(ORDER_NOT_FOUND)
        var order = orderOpt.get()
        if(!authorized && order.userId != userId.toLong())
            throw RuntimeException(UNAUTHORIZED)
        if (!authorized && updateOrderRequestDTO.status != null){
            throw RuntimeException(UNAUTHORIZED)
        }

        if (updateOrderRequestDTO.deliveryAddress != null) {
            if(order.orderStatus != OrderStatus.ISSUED && order.orderStatus != OrderStatus.PENDING)
                throw RuntimeException(ORDER_NOT_UPDATABLE)
            order.deliveryAddress = updateOrderRequestDTO.deliveryAddress
        }

        if(authorized && updateOrderRequestDTO.status != null){
            if(updateOrderRequestDTO.status == OrderStatus.CANCELED)
                throw RuntimeException(ORDER_NOT_CANCELABLE)

            order.updateStatus(updateOrderRequestDTO.status)

            val mail = MailDTO(
                order.userId.toString(), null,
                "Your order's status has been correctly updated: $orderId",
                "The order's status has been correctly updated to ${order.orderStatus}"
            )
            messageService.publish(mail, "OrderStatusUpdated", mailTopic)

        }
        order = orderRepository.save(order)

        return order.toOrderDTO()
    }

    override fun deleteOrder(orderId: Long) {
        if (orderRepository.existsById(orderId)) {
            val order = orderRepository.findById(orderId).get()
            order.items.forEach { item -> orderItemRepository.delete(item) }
            orderRepository.delete(order)
        }
        else
            throw RuntimeException(ORDER_NOT_FOUND)
    }

    override fun processOrderCompletion(orderStatusDTO: OrderStatusDTO, id: String, eventType: EventTypeOrderStatus) {
        println("process order completion")
        val eventID = UUID.fromString(id)

        if(processingLogService.isProcessed(eventID))
            return

        //val orderId = orderStatusDTO.orderID.parseID()
        val order = getOrderEntityOrThrowException(orderStatusDTO.orderID)
        when(orderStatusDTO.responseStatus) {
            ResponseStatus.COMPLETED -> {
                println("ORDER COMPLETED - set order status = ISSUED")
                // - set status to ISSUED
                order.updateStatus(OrderStatus.ISSUED)

                // - send email
                val mail = MailDTO(
                    order.userId.toString(), null,
                    "Your order has been issued: ${orderStatusDTO.orderID.parseID()}",
                    "The order has been correctly issued"
                )
                messageService.publish(mail, "OrderIssued", mailTopic)

                //notifyAdmin(order)

                orderRepository.save(order)

            }
            ResponseStatus.FAILED -> {
                println("ORDER FAILED - set order status = FAILED")
                // - set status to FAILED
                order.updateStatus(OrderStatus.FAILED)
                if (eventType == EventTypeOrderStatus.OrderPaymentFailed) {
                    // - if payment error rollback warehouse
                    val request = WarehouseOrderRequestCancelDTO(orderStatusDTO.orderID,
                        order.items.extractProductInWarehouse { PurchasedProductDTO(it.productId!!, it.amount!!)})
                    messageService.publish(request,  "OrderCancel", orderRequestTopic)
                }

                // - send email
                val mail: MailDTO = MailDTO(
                    order.userId.toString(), null,
                    "Your order has failed: ${orderStatusDTO.orderID}",
                    "The order was not issued.\nError message: ${orderStatusDTO.errorMessage}"
                )
                messageService.publish(mail, "OrderIssued", mailTopic)

                orderRepository.save(order)
            }
        }
        processingLogService.process(eventID)

    }

    override fun process(orderDetailsDTO: OrderDetailsDTO, id: String) {
        val eventID = UUID.fromString(id)
        if(processingLogService.isProcessed(eventID))
            return

        val order = getOrderEntityOrThrowException(orderDetailsDTO.orderId)
        orderDetailsDTO.productWarehouseList.forEach {
                productWarehouseDTO ->
            orderItemRepository.setWarehouseByOrderAndProduct(order, productWarehouseDTO.productId.toLong(), productWarehouseDTO.warehouseId.toLong() )
        }
        processingLogService.process(eventID)
    }

    private fun getOrderEntityOrThrowException(orderId: String): Order {
        return orderRepository.findByIdOrNull(orderId.parseID()) ?: throw RuntimeException(ORDER_NOT_FOUND)
    }

}