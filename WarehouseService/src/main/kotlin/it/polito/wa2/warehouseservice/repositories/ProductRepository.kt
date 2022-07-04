package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.entities.Category
import it.polito.wa2.warehouseservice.entities.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ProductRepository : PagingAndSortingRepository<Product, Long>{
    @Transactional
    fun findAllByCategory(category: Category, paging: Pageable): Page<Product>
}