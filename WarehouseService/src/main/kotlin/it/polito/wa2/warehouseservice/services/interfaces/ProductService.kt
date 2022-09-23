package it.polito.wa2.warehouseservice.services.interfaces

import it.polito.wa2.warehouseservice.dtos.AddCommentDTO
import it.polito.wa2.warehouseservice.dtos.ProductDTO
import it.polito.wa2.warehouseservice.dtos.CommentDTO
import it.polito.wa2.warehouseservice.dtos.ResponseProductDTO
import it.polito.wa2.warehouseservice.entities.Product
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile

interface ProductService {
    fun getProducts(category: String?, pageNo: Int, pageSize: Int): Page<ResponseProductDTO>
    fun getProductById(productId: Long): ResponseProductDTO
    fun getProductEntityById(productId: Long) : Product
    fun createProduct(productDTO: ProductDTO): ResponseProductDTO
    fun updateOrCreateProduct(productId: Long, productDTO: ProductDTO): ResponseProductDTO
    fun updateProduct(productId: Long, productDTO: ProductDTO): ResponseProductDTO
    fun deleteProduct(productId: Long)
    fun addPicture(productId: Long, picture: MultipartFile)
    fun getPicture(productId: Long): ByteArray
    fun addComment(commentDTO: AddCommentDTO) : CommentDTO
    fun getCommentsByProductId(productId: Long, pageNo: Int, pageSize: Int) : Page<CommentDTO>
}