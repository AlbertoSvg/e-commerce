package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.AddCommentDTO
import it.polito.wa2.warehouseservice.dtos.ProductDTO
import it.polito.wa2.warehouseservice.dtos.CommentDTO
import it.polito.wa2.warehouseservice.dtos.ResponseProductDTO
import it.polito.wa2.warehouseservice.entities.Product
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

    override fun getProducts(category: String?, pageNo: Int, pageSize: Int): Page<ResponseProductDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        val products: Page<Product>

        if (category == null) {
            products = productRepository.findAll(paging)
        }
        else
            products = productRepository.findAllByCategory(category, paging)
        return products.map { it.toProductDTO() }
    }

    override fun getProductById(productId: Long): ResponseProductDTO {
        val product = productRepository.findById(productId)
        if (product.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        return product.get().toProductDTO()
    }

    override fun createProduct(productDTO: ProductDTO): ResponseProductDTO {
        val product = Product().also {
            it.name = productDTO.name
            it.category = productDTO.category
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
        if (productOpt.isPresent) {
            product = productOpt.get()
            product.also {
                it.name = productDTO.name
                it.category = productDTO.category
                it.description = productDTO.description
                it.price = productDTO.price
            }
        }
        else {
            product = Product().also {
                it.name = productDTO.name
                it.category = productDTO.category
                it.description = productDTO.description
                it.price = productDTO.price
            }
        }
        return productRepository.save(product).toProductDTO()
    }

    override fun updateProduct(productId: Long, responseProductDTO: ProductDTO): ResponseProductDTO {
        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()
        if (responseProductDTO.name != null) product.name = responseProductDTO.name
        if (responseProductDTO.category != null) product.category = responseProductDTO.category
        if (responseProductDTO.description != null) product.description = responseProductDTO.description
        if (responseProductDTO.price != null) product.price = responseProductDTO.price
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

    override fun addComment(comment: AddCommentDTO): CommentDTO {
        TODO("Not yet implemented")
        //per implementarlo dobbiamo fare prima OrderService per gestire i PurchasedProducts
    }

    override fun getCommentsByProductId(productId: Long, pageNo: Int, pageSize: Int): Page<ResponseProductDTO> {
        TODO("Not yet implemented")
        //per implementarlo dobbiamo fare prima OrderService per gestire i PurchasedProducts
    }
}