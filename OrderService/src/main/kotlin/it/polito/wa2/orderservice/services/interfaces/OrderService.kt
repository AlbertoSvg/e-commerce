package it.polito.wa2.orderservice.services.interfaces

import it.polito.wa2.orderservice.dtos.OrderDTO
import it.polito.wa2.orderservice.dtos.order.request.*
import org.springframework.data.domain.Page

interface OrderService {
    fun getOrders(pageNo: Int, pageSize: Int): Page<OrderDTO>
    fun getOrderById(orderId: Long): OrderDTO
    fun createOrder(orderDTO: OrderRequestDTO): OrderDTO
    fun updateOrder(userId: String, orderId: Long, updateOrderRequestDTO: UpdateOrderRequestDTO, authorized: Boolean): OrderDTO
    fun deleteOrder(orderId: Long)
    fun getCustomerOrders(userId: String, pageNo: Int, pageSize: Int): Page<OrderDTO>
    fun processOrderCompletion(orderStatusDTO: OrderStatusDTO, id: String, eventType: EventTypeOrderStatus)
    fun process(orderDetailsDTO: OrderDetailsDTO, id: String)
}
