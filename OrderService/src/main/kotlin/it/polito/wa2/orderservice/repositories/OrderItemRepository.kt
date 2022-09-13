package it.polito.wa2.orderservice.repositories

import it.polito.wa2.orderservice.entities.Order
import it.polito.wa2.orderservice.entities.OrderItem
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface OrderItemRepository: PagingAndSortingRepository<OrderItem, Long> {
    @Transactional
    fun deleteAllByOrder(order: Order)

    @Modifying
    @Query("""update OrderItem 
            set warehouseId = :warehouse_id
            where order = :order and productId = :product_id""")
    fun setWarehouseByOrderAndProduct(@Param("order") order: Order,
                                      @Param("product_id") productId: Long,
                                      @Param("warehouse_id") warehouseId: Long)
}