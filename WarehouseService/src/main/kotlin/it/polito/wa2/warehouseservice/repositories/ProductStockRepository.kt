package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.entities.Product
import it.polito.wa2.warehouseservice.entities.ProductStock
import it.polito.wa2.warehouseservice.entities.Warehouse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface ProductStockRepository: PagingAndSortingRepository<ProductStock, Long>{
    @Transactional(readOnly = true)
    fun findAllByProductId(productId: Long, page: Pageable): Page<ProductStock>

    @Transactional
    fun findProductStockByWarehouseAndProduct(warehouse: Warehouse, product: Product): Optional<ProductStock>
}