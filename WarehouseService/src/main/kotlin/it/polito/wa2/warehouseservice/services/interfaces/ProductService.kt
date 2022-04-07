package it.polito.wa2.warehouseservice.services.interfaces

import it.polito.wa2.warehouseservice.dtos.ProductDTO
import org.springframework.data.domain.Page

interface ProductService {
    fun getProducts(category: String?, pageNo: Int, pageSize: Int): Page<ProductDTO>
    fun getProductById(productId: Long): ProductDTO
    fun createProduct(productDTO: ProductDTO): ProductDTO
    fun updateOrCreateProduct(productId: Long, productDTO: ProductDTO): ProductDTO
    fun updateProduct(productId: Long, productDTO: ProductDTO): ProductDTO
    fun deleteProduct(productId: Long)
}