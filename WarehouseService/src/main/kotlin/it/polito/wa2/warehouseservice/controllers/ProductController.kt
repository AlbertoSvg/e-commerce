package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.AddCommentDTO
import it.polito.wa2.warehouseservice.dtos.OrderCheckDTO
import it.polito.wa2.warehouseservice.dtos.ProductDTO
import it.polito.wa2.warehouseservice.services.interfaces.ProductService
import it.polito.wa2.warehouseservice.services.interfaces.WarehouseService
import it.polito.wa2.warehouseservice.validators.validatePatch
import it.polito.wa2.warehouseservice.validators.validatePost
import it.polito.wa2.warehouseservice.validators.validatePut
import it.polito.wa2.warehouseservice.webclient.ClientRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/products")
class ProductController {

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var warehouseService: WarehouseService

    @Autowired
    private lateinit var request: ClientRequest

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
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
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
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
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
            return ResponseEntity.badRequest().body(e.message)
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
            return ResponseEntity.badRequest().body(e.message)
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
            return ResponseEntity.badRequest().body(e.message)
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
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{productId}/picture")
    fun getProductPicture(
        @PathVariable("productId") productId: Long
    ): ResponseEntity<Any> {
        try {
            val response = productService.getPicture(productId)
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(response)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/{productId}/picture")
    fun addPicture(
        @PathVariable("productId") productId: Long,
        @RequestParam("file") picture: MultipartFile
    ): ResponseEntity<Any> {
        try {
            productService.addPicture(productId, picture)
            return ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{productId}/warehouses")
    fun getWarehousesWithStock(
        @PathVariable("productId") productId: Long,
        @RequestParam("pageNo", defaultValue = Values.DEFAULT_PAGE_NO) @Min(0) pageNo: Int,
        @RequestParam("pageSize", defaultValue = Values.DEFAULT_PAGE_SIZE) @Min(1) pageSize: Int,
    ): ResponseEntity<Any> {
        try {
            val warehousePageDTO = warehouseService.getWarehouses(productId, pageNo, pageSize)
            val response = hashMapOf<String, Any>()
            response["warehouses"] = warehousePageDTO.content
            response["currentPage"] = pageNo
            response["totalItems"] = warehousePageDTO.totalElements
            response["totalPages"] = warehousePageDTO.totalPages
            return ResponseEntity.ok(response)
        } catch(e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }


    @PostMapping("/comments")

    fun addComment(
        @RequestBody @Valid comment: AddCommentDTO,
        @RequestHeader("userId") userId : Long
    ): ResponseEntity<Any> {
        try {
            val uri = "http://order-service:8300/orders/"+comment.productId.toString()+"/buyers/"+userId
            val orderCheckDTO = request.doGet(uri, OrderCheckDTO::class.java)
            if (orderCheckDTO.ok == true) {
                return ResponseEntity.status(HttpStatus.CREATED).body(productService.addComment(comment, userId))
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product NOT purchased")
            }
        } catch(e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{productId}/comments")
    @ResponseStatus(HttpStatus.OK)
    fun getComments(@PathVariable("productId") productId: Long,
                    @RequestParam("pageNo", defaultValue = Values.DEFAULT_PAGE_NO) @Min(0) pageNo: Int,
                    @RequestParam("pageSize", defaultValue = Values.DEFAULT_PAGE_SIZE) @Min(1) pageSize: Int
    ): ResponseEntity<Any> {
        return try{
            val comments =  productService.getCommentsByProductId(productId, pageNo, pageSize)
            val response = hashMapOf<String, Any>()
            response["comments"] = comments.content
            response["currentPage"] = pageNo
            response["totalItems"] = comments.totalElements
            response["totalPages"] = comments.totalPages
            ResponseEntity.ok(response)
        } catch(e: RuntimeException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

}