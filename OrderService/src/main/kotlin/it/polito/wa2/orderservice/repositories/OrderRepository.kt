package it.polito.wa2.orderservice.repositories

import it.polito.wa2.orderservice.entities.Order
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: PagingAndSortingRepository<Order, Long> {}