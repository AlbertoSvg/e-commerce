package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.entities.Product
import it.polito.wa2.warehouseservice.entities.ProductStock
import it.polito.wa2.warehouseservice.entities.Warehouse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface ProductStockRepository: PagingAndSortingRepository<ProductStock, Long>{
    @Transactional(readOnly = true)
    fun findAllByProductId(productId: Long, page: Pageable): Page<ProductStock>

    @Transactional
    fun findProductStockByWarehouseAndProduct(warehouse: Warehouse, product: Product): Optional<ProductStock>

    @Query("SELECT sum(p.productQty) FROM ProductStock p WHERE p.product.id = :productId")
    fun getTotalQuantityByProductId(@Param("productId") productId: Long) : Long

    @Transactional(readOnly = true)
    fun findAllByProductAndProductQtyIsGreaterThanEqual(product: Product, productQty: Long): List<ProductStock>

    @Transactional(readOnly = true)
    override fun findAll(): List<ProductStock>

}