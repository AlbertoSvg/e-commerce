package it.polito.wa2.orderservice.services.interfaces

import it.polito.wa2.orderservice.dtos.OrderDTO
import org.springframework.data.domain.Page

interface OrderService {
    fun getOrders(pageNo: Int, pageSize: Int): Page<OrderDTO>
    fun getOrderById(orderId: Long): OrderDTO
    fun createOrder(orderDTO: OrderDTO): OrderDTO
    fun updateOrder(orderId: Long, orderDTO: OrderDTO): OrderDTO
    fun deleteOrder(orderId: Long)
    fun getCustomerOrders(userId: String, pageNo: Int, pageSize: Int): Page<OrderDTO>
}
