package it.polito.wa2.orderservice.services.implementations

import it.polito.wa2.orderservice.constants.OrderStatus
import it.polito.wa2.orderservice.constants.Values
import it.polito.wa2.orderservice.constants.Values.ORDER_NOT_FOUND
import it.polito.wa2.orderservice.dtos.OrderDTO
import it.polito.wa2.orderservice.dtos.order.request.EventTypeOrderStatus
import it.polito.wa2.orderservice.dtos.order.request.OrderDetailsDTO
import it.polito.wa2.orderservice.dtos.order.request.OrderStatusDTO
import it.polito.wa2.orderservice.entities.Order
import it.polito.wa2.orderservice.repositories.OrderItemRepository
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.orderservice.services.interfaces.OrderService
import it.polito.wa2.saga.services.MessageService
import it.polito.wa2.saga.services.ProcessingLogService
import it.polito.wa2.saga.utils.parseID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    override fun createOrder(orderDTO: OrderDTO): OrderDTO {
        var order = Order().also {
            it.userId = orderDTO.userId
            it.walletId = orderDTO.walletId
            it.deliveryAddress = orderDTO.deliveryAddress
            it.orderStatus = OrderStatus.PENDING
        }
        order = orderRepository.save(order)
        orderDTO.items?.forEach { item ->
            val orderItem = item.toOrderItemEntity()
            orderItem.order = order
            order.items.add(orderItem)
        }
        orderItemRepository.saveAll(order.items)
        return order.toOrderDTO()
    }

    override fun updateOrder(orderId: Long, orderDTO: OrderDTO): OrderDTO {
        val orderOpt = orderRepository.findById(orderId)
        if (orderOpt.isEmpty) throw RuntimeException(Values.ORDER_NOT_FOUND)
        var order = orderOpt.get()
        if (orderDTO.walletId != null) order.walletId = orderDTO.walletId
        if (orderDTO.deliveryAddress != null) order.deliveryAddress = orderDTO.deliveryAddress
        if (orderDTO.status != null) {
            if (orderDTO.status == OrderStatus.CANCELED && order.orderStatus != OrderStatus.ISSUED && order.orderStatus != OrderStatus.PENDING)
                throw RuntimeException(Values.ORDER_NOT_CANCELABLE)
            else
                order.orderStatus = orderDTO.status
        }
        order = orderRepository.save(order)
        if (orderDTO.items != null) {
            order.items.clear()
            orderItemRepository.deleteAllByOrder(order)
            orderDTO.items.forEach { item ->
                val orderItem = item.toOrderItemEntity()
                orderItem.order = order
                order.items.add(orderItem)
            }
            orderItemRepository.saveAll(order.items)
        }
        return order.toOrderDTO()
    }

    override fun deleteOrder(orderId: Long) {
        if (orderRepository.existsById(orderId)) {
            val order = orderRepository.findById(orderId).get()
            order.items.forEach { item -> orderItemRepository.delete(item) }
            orderRepository.delete(order)
        }
        else
            throw RuntimeException(Values.ORDER_NOT_FOUND)
    }

    override fun processOrderCompletion(orderStatusDTO: OrderStatusDTO, id: String, eventType: EventTypeOrderStatus) {
//        //TODO: DA FINIRE
//
//        val eventID = UUID.fromString(id)
//        if(processingLogService.isProcessed(eventID))
//            return
//
//        //val orderId = orderStatusDTO.orderID.parseID()
//        val order = getOrderEntityOrThrowException(orderStatusDTO.orderID)
//        when(orderStatusDTO.responseStatus) {
//            ResponseStatus.COMPLETED -> {
//
//                // - set status to ISSUED
//                order.updateStatus(Status.ISSUED)
//
//                // - send email
//                val mail: MailDTO = MailDTO(
//                    order.buyerId, null,
//                    "Your order has been issued: $orderId",
//                    "The order has been correctly issued"
//                )
//                //messageService.publish(mail, "OrderIssued", mailTopic)
//
//                notifyAdmin(order)
//
//                orderRepository.save(order)
//
//            }
//            ResponseStatus.FAILED -> {
//
//                // - set status to FAILED
//                order.updateStatus(Status.FAILED)
//                if (eventType == EventTypeOrderStatus.OrderPaymentFailed) {
//                    // - if payment error rollback warehouse
//                    val request = WarehouseOrderRequestCancelDTO(orderId.toString(),
//                        order.deliveryItems.extractProductInWarehouse { ItemDTO(it.productId, it.amount) })
//
//                    messageService.publish(request,  "OrderCancel", orderRequestTopic)
//                }
//
//                // - send email
//                val mail: MailDTO = MailDTO(
//                    order.buyerId, null,
//                    "Your order has failed: $orderId",
//                    "The order was not issued.\nError message: ${orderStatusDTO.errorMessage}"
//                )
//                //messageService.publish(mail, "OrderIssued", mailTopic)
//
//                orderRepository.save(order)
//            }
//        }
//        processingLogService.process(eventID)

    }

    override fun process(orderDetailsDTO: OrderDetailsDTO, id: String) {
//        //TODO: DA FINIRE
//        val eventID = UUID.fromString(id)
//        if(processingLogService.isProcessed(eventID))
//            return
//
//        val order = getOrderEntityOrThrowException(orderDetailsDTO.orderId)
//        orderDetailsDTO.productWarehouseList.forEach {
//                productWarehouseDTO ->
//            purchaseItemRepository.setWarehouseByOrderAndProduct(order, productWarehouseDTO.productId, productWarehouseDTO.warehouseId )
//
//        }
//        processingLogService.process(eventID)
    }

    private fun getOrderEntityOrThrowException(orderId: String): Order {
        return orderRepository.findByIdOrNull(orderId.parseID()) ?: throw RuntimeException(ORDER_NOT_FOUND)
    }

}