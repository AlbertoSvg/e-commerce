package it.polito.wa2.warehouseservice.services.implementations

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.ProductDTO
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

    override fun getProducts(category: String?, pageNo: Int, pageSize: Int): Page<ProductDTO> {
        val paging = PageRequest.of(pageNo, pageSize)
        val products: Page<Product>

        if (category == null) {
            products = productRepository.findAll(paging)
        }
        else
            products = productRepository.findAllByCategory(category, paging)
        return products.map { it.toProductDTO() }
    }

    override fun getProductById(productId: Long): ProductDTO {
        val product = productRepository.findById(productId)
        if (product.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        return product.get().toProductDTO()
    }

    override fun createProduct(productDTO: ProductDTO): ProductDTO {
        val product = Product().also {
            it.category = productDTO.category
            it.description = productDTO.description
            it.price = productDTO.price
        }
        return productRepository.save(product).toProductDTO()
    }

    override fun updateOrCreateProduct(productId: Long, productDTO: ProductDTO): ProductDTO {
        val productOpt = productRepository.findById(productId)
        val product: Product
        if (productOpt.isPresent) {
            product = productOpt.get()
            product.also {
                it.category = productDTO.category
                it.description = productDTO.description
                it.price = productDTO.price
            }
        }
        else {
            product = Product().also {
                it.category = productDTO.category
                it.description = productDTO.description
                it.price = productDTO.price
            }
        }
        return productRepository.save(product).toProductDTO()
    }

    override fun updateProduct(productId: Long, productDTO: ProductDTO): ProductDTO {
        val productOpt = productRepository.findById(productId)
        if (productOpt.isEmpty) throw RuntimeException(Values.PRODUCT_NOT_FOUND)
        val product = productOpt.get()
        if (productDTO.category != null) product.category = productDTO.category
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
}