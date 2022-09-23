package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.constants.Values.CATEGORY_NOT_FOUND
import it.polito.wa2.warehouseservice.dtos.AddCommentDTO
import it.polito.wa2.warehouseservice.dtos.ProductDTO
import it.polito.wa2.warehouseservice.dtos.CommentDTO
import it.polito.wa2.warehouseservice.dtos.ResponseProductDTO
import it.polito.wa2.warehouseservice.entities.Category
import it.polito.wa2.warehouseservice.entities.Comment
import it.polito.wa2.warehouseservice.entities.Product
import it.polito.wa2.warehouseservice.repositories.CommentRepository
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import it.polito.wa2.warehouseservice.repositories.ProductStockRepository
import it.polito.wa2.warehouseservice.services.interfaces.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class ProductServiceImpl: ProductService {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var productStockRepository: ProductStockRepository

    @Autowired
    private lateinit var commentRepository: CommentRepository

    override fun getProducts(category: String?, pageNo: Int, pageSize: Int): Page<ResponseProductDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        val products: Page<Product>

        if (category == null) {
            products = productRepository.findAll(paging)
        }
        else {
            val categoryName : Category
            try {
                categoryName = Category.valueOf(category)
            } catch (e: IllegalArgumentException){
                throw RuntimeException(CATEGORY_NOT_FOUND)
            }
            products = productRepository.findAllByCategory(categoryName, paging)
        }
        return products.map { p -> p.toProductDTO().also { it.totalProductQty = productStockRepository.getTotalQuantityByProductId(it.id!!)} }
    }

    override fun getProductById(productId: Long): ResponseProductDTO {
        val product = productRepository.findById(productId)
        if (product.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        return product.get().toProductDTO().also { it.totalProductQty = productStockRepository.getTotalQuantityByProductId(it.id!!) }
    }

    override fun getProductEntityById(productId: Long): Product {
        val product = productRepository.findById(productId)
        if (product.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        return product.get()
    }

    override fun createProduct(productDTO: ProductDTO): ResponseProductDTO {
        val categoryName : Category
        try {
            categoryName = Category.valueOf(productDTO.category!!)
        } catch (e: IllegalArgumentException){
            throw RuntimeException(CATEGORY_NOT_FOUND)
        }
        val product = Product().also {
            it.name = productDTO.name
            it.category = categoryName
            it.description = productDTO.description
            it.price = productDTO.price
            it.numRatings = 0
            it.numStars = 0
        }
        return productRepository.save(product).toProductDTO()
    }

    override fun updateOrCreateProduct(productId: Long, productDTO: ProductDTO): ResponseProductDTO {
        val productOpt = productRepository.findById(productId)
        val product: Product
        val categoryName : Category
        try {
            categoryName = Category.valueOf(productDTO.category!!)
        } catch (e: IllegalArgumentException){
            throw RuntimeException(CATEGORY_NOT_FOUND)
        }
        if (productOpt.isPresent) {
            product = productOpt.get()

            product.also {
                it.name = productDTO.name
                it.category = categoryName
                it.description = productDTO.description
                it.price = productDTO.price
            }
        }
        else {
            product = Product().also {
                it.name = productDTO.name
                it.category = categoryName
                it.description = productDTO.description
                it.price = productDTO.price
            }
        }
        return productRepository.save(product).toProductDTO()
    }

    override fun updateProduct(productId: Long, productDTO: ProductDTO): ResponseProductDTO {
        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()
        if (productDTO.name != null) product.name = productDTO.name
        if (productDTO.category != null) {
            val categoryName : Category
            try {
                categoryName = Category.valueOf(productDTO.category)
            } catch (e: IllegalArgumentException){
                throw RuntimeException(CATEGORY_NOT_FOUND)
            }
            product.category = categoryName
        }
        if (productDTO.description != null) product.description = productDTO.description
        if (productDTO.price != null) product.price = productDTO.price
        return productRepository.save(product).toProductDTO()
    }

    override fun deleteProduct(productId: Long) {
        if (productRepository.existsById(productId))
            productRepository.deleteById(productId)
        else
            throw RuntimeException(Values.PRODUCT_NOT_FOUND)
    }

    override fun addPicture(productId: Long, picture: MultipartFile) {
        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()
        product.picture = picture.bytes
        productRepository.save(product)
    }

    override fun getPicture(productId: Long): ByteArray {
        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()
        if (product.picture == null) throw RuntimeException(Values.PICTURE_NOT_FOUND)
        else return product.picture!!
    }

    override fun addComment(commentDTO: AddCommentDTO): CommentDTO {
        val productOpt = productRepository.findById(commentDTO.productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val comment = Comment().also {
            it.product = productOpt.get()
            it.body = commentDTO.body
            it.title = commentDTO.title
            it.stars = commentDTO.stars
        }
        return commentRepository.save(comment).toCommentDTO()
    }

    override fun getCommentsByProductId(productId: Long, pageNo: Int, pageSize: Int): Page<CommentDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val comments = commentRepository.findAllByProductId(productId, paging)
        return comments.map{c -> c.toCommentDTO()}
    }
}