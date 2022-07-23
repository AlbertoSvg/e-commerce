package it.polito.wa2.orderservice.repositories

import it.polito.wa2.orderservice.entities.Order
import it.polito.wa2.orderservice.entities.OrderItem
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface OrderItemRepository: PagingAndSortingRepository<OrderItem, Long> {
    @Transactional
    fun deleteAllByOrder(order: Order)
}