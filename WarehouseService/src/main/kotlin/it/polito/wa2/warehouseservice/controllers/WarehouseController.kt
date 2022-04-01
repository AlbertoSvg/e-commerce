package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.constants.Values
import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import it.polito.wa2.warehouseservice.services.interfaces.WarehouseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@RestController
@RequestMapping("/warehouses")
class WarehouseController {

    @Autowired
    private lateinit var warehouseService: WarehouseService

    @GetMapping("/")
    fun getWarehouses(
        @RequestParam("productId", required = false) productId: Long?,
        @RequestParam("pageNo", defaultValue = Values.DEFAULT_PAGE_NO) @Min(1) pageNo: Int,
        @RequestParam("pageSize", defaultValue = Values.DEFAULT_PAGE_SIZE) @Min(1) pageSize: Int,
    ): ResponseEntity<Any> {
        return try {
            val warehousePageDTO = warehouseService.getWarehouses(productId, pageNo, pageSize)
            val response = hashMapOf<String, Any>()
            response["warehouses"] = warehousePageDTO.content
            response["currentPage"] = pageNo
            response["totalItems"] = warehousePageDTO.totalElements
            response["totalPages"] = warehousePageDTO.totalPages
            ResponseEntity.ok(response)
        } catch(e: RuntimeException) { //TODO: diversificazione eccezioni
            ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @GetMapping("/{warehouseId}")
    fun getWarehouseById(
        @PathVariable("warehouseId") warehouseId: Long
    ): ResponseEntity<Any> {
        return try {
            val warehouseDTO = warehouseService.getWarehouseById(warehouseId)
            ResponseEntity.ok(warehouseDTO)
        } catch(e: RuntimeException) { //TODO: diversificazione eccezioni
            ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @PostMapping("/")
    fun createWarehouse(
        @RequestBody warehouseDTO: WarehouseDTO,
    ): ResponseEntity<Any> {
        try {
            val returnedWarehouseDTO = warehouseService.createWarehouse(warehouseDTO.name!!)
            return ResponseEntity.status(HttpStatus.CREATED).body(returnedWarehouseDTO)
        } catch (e: RuntimeException) { //TODO: diversificazione eccezioni
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    @PutMapping("/{warehouseId}")
    fun updateOrCreateWarehouse(
        @PathVariable("warehouseId") warehouseId: Long,
        @RequestBody warehouseDTO: WarehouseDTO,
    ): ResponseEntity<Any> {
        try {
            val returnedWarehouseDTO = warehouseService.updateOrCreateWarehouse(warehouseId, warehouseDTO.name!!)
            return ResponseEntity.ok(returnedWarehouseDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }

    /* TODO
    @PatchMapping("/{warehouseId}")
    fun updateWarehouse(
        @PathVariable("warehouseId") warehouseId: Long,
        @RequestBody warehouseDTO: WarehouseDTO
    ): ResponseEntity<Any> {
        try {
            val returnedWarehouseDTO = warehouseService.updateWarehouse(warehouseDTO.name)
            return ResponseEntity.ok(returnedWarehouseDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }
    */

    @DeleteMapping("/{warehouseId}")
    fun deleteWarehouse(
        @PathVariable("warehouseId") warehouseId: Long
    ): ResponseEntity<Any> {
        try {
            warehouseService.deleteWarehouse(warehouseId)
            return ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(Values.EXCEPTION_OCCURRED)
        }
    }
    //TODO: implement DTO validation with annotations or code for update, updateOrCreate, create, might need
    // additional DTO
}