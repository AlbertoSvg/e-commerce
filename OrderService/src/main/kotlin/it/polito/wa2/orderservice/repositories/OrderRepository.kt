package it.polito.wa2.orderservice.repositories

import it.polito.wa2.orderservice.entities.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface OrderRepository: PagingAndSortingRepository<Order, Long> {
    @Transactional(readOnly = true)
    fun findAllByUserId(userId: Long, page: Pageable): Page<Order>
    fun findAllByUserId(userId: Long): List<Order>
}