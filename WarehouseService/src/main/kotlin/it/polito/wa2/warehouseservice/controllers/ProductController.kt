package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.ProductDTO
import it.polito.wa2.warehouseservice.services.interfaces.ProductService
import it.polito.wa2.warehouseservice.validators.validatePatch
import it.polito.wa2.warehouseservice.validators.validatePost
import it.polito.wa2.warehouseservice.validators.validatePut
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Min

@RestController
@RequestMapping("/products")
class ProductController {
    //@Autowired
    private lateinit var productService: ProductService

    @GetMapping
    fun getProducts(
        @RequestParam("category", required = false) category: String?,
        @RequestParam("pageNo", defaultValue = Values.DEFAULT_PAGE_NO) @Min(0) pageNo: Int,
        @RequestParam("pageSize", defaultValue = Values.DEFAULT_PAGE_SIZE) @Min(1) pageSize: Int
    ): ResponseEntity<Any> {
        return try {
            val productPageDTO = productService.getProducts(category, pageNo, pageSize)
            val response = hashMapOf<String, Any>()
            response["products"] = productPageDTO.content
            response["currentPage"] = pageNo
            response["totalItems"] = productPageDTO.totalElements
            response["totalPages"] = productPageDTO.totalPages
            ResponseEntity.ok(response)
        } catch(e: RuntimeException) {
            ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @GetMapping("/{productId}")
    fun getProductById(
        @PathVariable("productId") productId: Long
    ): ResponseEntity<Any> {
        return try {
            val productDTO = productService.getProductById(productId)
            ResponseEntity.ok(productDTO)
        } catch(e: RuntimeException) {
            ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @PostMapping
    fun createProduct(
        @RequestBody productDTO: ProductDTO,
    ): ResponseEntity<Any> {
        try {
            if (!productDTO.validatePost()) return ResponseEntity.badRequest().body(Values.INVALID_PRODUCT_REPRESENTATION)
            val responseProductDTO = productService.createProduct(productDTO)
            return ResponseEntity.status(HttpStatus.CREATED).body(responseProductDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @PutMapping("/{productId}")
    fun updateOrCreateProduct(
        @PathVariable("productId") productId: Long,
        @RequestBody productDTO: ProductDTO,
    ): ResponseEntity<Any> {
        try {
            if (!productDTO.validatePut()) return ResponseEntity.badRequest().body(Values.INVALID_PRODUCT_REPRESENTATION)
            val responseProductDTO = productService.updateOrCreateProduct(productId, productDTO)
            return ResponseEntity.ok(responseProductDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @PatchMapping("/{productId}")
    fun updateProduct(
        @PathVariable("productId") productId: Long,
        @RequestBody productDTO: ProductDTO
    ): ResponseEntity<Any> {
        try {
            if (!productDTO.validatePatch()) return ResponseEntity.badRequest().body(Values.INVALID_PRODUCT_REPRESENTATION)
            val responseProductDTO = productService.updateProduct(productId, productDTO)
            return ResponseEntity.ok(responseProductDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @PathVariable("productId") productId: Long
    ): ResponseEntity<Any> {
        try {
            productService.deleteProduct(productId)
            return ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }
}