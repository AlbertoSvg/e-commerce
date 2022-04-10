package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import it.polito.wa2.warehouseservice.services.interfaces.WarehouseService
import it.polito.wa2.warehouseservice.validators.validatePatch
import it.polito.wa2.warehouseservice.validators.validatePost
import it.polito.wa2.warehouseservice.validators.validatePut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Min

@RestController
@RequestMapping("/warehouses")
class WarehouseController {

    @Autowired
    private lateinit var warehouseService: WarehouseService

    @GetMapping
    fun getWarehouses(
        @RequestParam("productId", required = false) productId: Long?,
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
        } catch(e: RuntimeException) { //TODO: diversificazione eccezioni
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{warehouseId}")
    fun getWarehouseById(
        @PathVariable("warehouseId") warehouseId: Long
    ): ResponseEntity<Any> {
        try {
            val warehouseDTO = warehouseService.getWarehouseById(warehouseId)
            return ResponseEntity.ok(warehouseDTO)
        } catch(e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping
    fun createWarehouse(
        @RequestBody warehouseDTO: WarehouseDTO,
    ): ResponseEntity<Any> {
        try {
            if (!warehouseDTO.validatePost()) return ResponseEntity.badRequest().body(Values.INVALID_WAREHOUSE_REPRESENTATION)
            val responseWarehouseDTO = warehouseService.createWarehouse(warehouseDTO)
            return ResponseEntity.status(HttpStatus.CREATED).body(responseWarehouseDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PutMapping("/{warehouseId}")
    fun updateOrCreateWarehouse(
        @PathVariable("warehouseId") warehouseId: Long,
        @RequestBody warehouseDTO: WarehouseDTO,
    ): ResponseEntity<Any> {
        try {
            if (!warehouseDTO.validatePut()) return ResponseEntity.badRequest().body(Values.INVALID_WAREHOUSE_REPRESENTATION)
            val responseWarehouseDTO = warehouseService.updateOrCreateWarehouse(warehouseId, warehouseDTO)
            return ResponseEntity.ok(responseWarehouseDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }


    @PatchMapping("/{warehouseId}")
    fun updateWarehouse(
        @PathVariable("warehouseId") warehouseId: Long,
        @RequestBody warehouseDTO: WarehouseDTO
    ): ResponseEntity<Any> {
        try {
            if (!warehouseDTO.validatePatch()) return ResponseEntity.badRequest().body(Values.INVALID_WAREHOUSE_REPRESENTATION)
            val responseWarehouseDTO = warehouseService.updateWarehouse(warehouseId, warehouseDTO)
            return ResponseEntity.ok(responseWarehouseDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }


    @DeleteMapping("/{warehouseId}")
    fun deleteWarehouse(
        @PathVariable("warehouseId") warehouseId: Long
    ): ResponseEntity<Any> {
        try {
            warehouseService.deleteWarehouse(warehouseId)
            return ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }
}