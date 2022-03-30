package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.entities.ProductStock
import it.polito.wa2.warehouseservice.entities.Warehouse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ProductStockRepository: PagingAndSortingRepository<ProductStock, Long>{
    @Transactional(readOnly = true)
    fun findAllByWarehouse(warehouse: Warehouse, page: Pageable): Page<ProductStock>

    @Transactional(readOnly = true)
    fun findByWarehouseAndProductId(warehouse: Warehouse, productId: Long): Optional<ProductStock>

    @Transactional(readOnly = true)
    fun findAllByProductId(productStockId: Long, page: Pageable): Page<ProductStock>

    @Transactional(readOnly = true)
    fun findAllByProductIdAndProductQtyIsGreaterThanEqual(productId: Long, productQty: Long): Page<ProductStock>
}