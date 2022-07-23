package it.polito.wa2.orderservice.controllers

import it.polito.wa2.orderservice.constants.Values
import it.polito.wa2.orderservice.dtos.OrderDTO
import it.polito.wa2.orderservice.services.interfaces.OrderService
import it.polito.wa2.orderservice.validators.validatePatch
import it.polito.wa2.orderservice.validators.validatePost
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Min


@RestController
@RequestMapping("/orders")
class OrderController {
    @Autowired
    private lateinit var orderService: OrderService

    @GetMapping
    fun getOrders(
        @RequestParam("pageNo", defaultValue = Values.DEFAULT_PAGE_NO) @Min(0) pageNo: Int,
        @RequestParam("pageSize", defaultValue = Values.DEFAULT_PAGE_SIZE) @Min(1) pageSize: Int,
    ) : ResponseEntity<Any> {
        try {
            val orderPageDTO = orderService.getOrders(pageNo, pageSize)
            val response = hashMapOf<String, Any>()
            response["orders"] = orderPageDTO.content
            response["currentPage"] = pageNo
            response["totalItems"] = orderPageDTO.totalElements
            response["totalPages"] = orderPageDTO.totalPages
            return ResponseEntity.ok(response)
        } catch(e: RuntimeException) { //TODO: diversificazione eccezioni
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{orderId}")
    fun getOrderById(
        @PathVariable("orderId") orderId: Long
    ): ResponseEntity<Any> {
        try {
            val orderDTO = orderService.getOrderById(orderId)
            return ResponseEntity.ok(orderDTO)
        } catch(e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping
    fun createOrder(
        @RequestBody orderDTO: OrderDTO,
    ): ResponseEntity<Any> {
        try {
            if (!orderDTO.validatePost()) return ResponseEntity.badRequest().body(Values.INVALID_ORDER_REPRESENTATION)
            val responseOrderDTO = orderService.createOrder(orderDTO)
            return ResponseEntity.status(HttpStatus.CREATED).body(responseOrderDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PatchMapping("/{orderId}")
    fun updateOrder(
        @PathVariable("orderId") orderId: Long,
        @RequestBody orderDTO: OrderDTO
    ): ResponseEntity<Any> {
        try {
            if (!orderDTO.validatePatch()) return ResponseEntity.badRequest().body(Values.INVALID_ORDER_REPRESENTATION)
            val responseOrderDTO = orderService.updateOrder(orderId, orderDTO)
            return ResponseEntity.ok(responseOrderDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/{orderId}")
    fun deleteProduct(
        @PathVariable("orderId") orderId: Long
    ): ResponseEntity<Any> {
        try {
            orderService.deleteOrder(orderId)
            return ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

}