package it.polito.wa2.orderservice.services.implementations

import it.polito.wa2.orderservice.constants.OrderStatus
import it.polito.wa2.orderservice.constants.Values
import it.polito.wa2.orderservice.dtos.OrderDTO
import it.polito.wa2.orderservice.entities.Order
import it.polito.wa2.orderservice.repositories.OrderItemRepository
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.orderservice.services.interfaces.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderServiceImpl: OrderService {

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
        if (order.isEmpty) throw RuntimeException(Values.ORDER_MOT_FOUND)
        return order.get().toOrderDTO()
    }

    override fun createOrder(orderDTO: OrderDTO): OrderDTO {
        var order = Order().also {
            it.userId = orderDTO.userId
            it.walletId = orderDTO.walletId
            it.deliveryAddress = orderDTO.deliveryAddress
            it.status = OrderStatus.PENDING
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
        if (orderOpt.isEmpty) throw RuntimeException(Values.ORDER_MOT_FOUND)
        var order = orderOpt.get()
        if (orderDTO.walletId != null) order.walletId = orderDTO.walletId
        if (orderDTO.userId != null) order.userId = orderDTO.userId
        if (orderDTO.deliveryAddress != null) order.deliveryAddress = orderDTO.deliveryAddress
        if (orderDTO.status != null) order.status = orderDTO.status
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
            if (order.status != OrderStatus.PENDING && order.status != OrderStatus.ISSUED)
                throw RuntimeException(Values.ORDER_NOT_CANCELABLE)
            orderRepository.delete(order)
        }
        else
            throw RuntimeException(Values.ORDER_MOT_FOUND)
    }


}