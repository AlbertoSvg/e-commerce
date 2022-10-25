package it.polito.wa2.orderservice.controllers

import it.polito.wa2.orderservice.constants.RoleName
import it.polito.wa2.orderservice.constants.Values
import it.polito.wa2.orderservice.dtos.OrderDTO
import it.polito.wa2.orderservice.dtos.order.request.OrderRequestDTO
import it.polito.wa2.orderservice.dtos.order.request.UpdateOrderRequestDTO
import it.polito.wa2.orderservice.services.interfaces.OrderService
import it.polito.wa2.orderservice.validators.validatePatch
import it.polito.wa2.orderservice.validators.validatePost
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
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
        @RequestHeader("userId") userId: String?,
        @RequestHeader("roles") roles: String?
    ) : ResponseEntity<Any> {
        try {
            val orderPageDTO: Page<OrderDTO>
            if (roles == null)
                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
            if (roles.contains(RoleName.ROLE_ADMIN.value))
                orderPageDTO = orderService.getOrders(pageNo, pageSize)
            else if (roles.contains(RoleName.ROLE_CUSTOMER.value) && userId != null)
                orderPageDTO = orderService.getCustomerOrders(userId, pageNo, pageSize)
            else
                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
            val response = hashMapOf<String, Any>()
            response["orders"] = orderPageDTO.content
            response["currentPage"] = pageNo
            response["totalItems"] = orderPageDTO.totalElements
            response["totalPages"] = orderPageDTO.totalPages
            return ResponseEntity.ok(response)
        } catch(e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/{productId}/buyers/{userId}")
    fun checkPurchase(
        @PathVariable("productId") productId: Long,
        @PathVariable("userId") userId: Long
    ): ResponseEntity<Any> {
        try {
            val orderCheckDTO = orderService.checkPurchase(productId, userId)
            return ResponseEntity.ok(orderCheckDTO)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body("Error")
        }
    }

    @GetMapping("/{orderId}")
    fun getOrderById(
        @PathVariable("orderId") orderId: Long,
        @RequestHeader("userId") userId: String?,
        @RequestHeader("roles") roles: String?
    ): ResponseEntity<Any> {
        try {
            if (roles == null)
                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
            if (roles.contains(RoleName.ROLE_ADMIN.value)) {
                val orderDTO = orderService.getOrderById(orderId)
                return ResponseEntity.ok(orderDTO)
            }
            else if (roles.contains(RoleName.ROLE_CUSTOMER.value) && userId != null) {
                val orderDTO = orderService.getOrderById(orderId)
                if (orderDTO.userId != userId.toLong())
                    return ResponseEntity.status(401).body(Values.UNAUTHORIZED)
                return ResponseEntity.ok(orderDTO)
            }
            else
                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
        } catch(e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping
    fun createOrder(
        @RequestBody orderDTO: OrderRequestDTO,
        @RequestHeader("userId") userId: String?,
        @RequestHeader("roles") roles: String?
    ): ResponseEntity<Any> {
        try {
            if (roles == null)
                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
            if (!orderDTO.validatePost())
                return ResponseEntity.badRequest().body(Values.INVALID_ORDER_REPRESENTATION)
            if (roles.contains(RoleName.ROLE_ADMIN.value)) {
                val responseOrderDTO = orderService.createOrder(orderDTO)
                return ResponseEntity.status(HttpStatus.CREATED).body(responseOrderDTO)
            }
            else if (roles.contains(RoleName.ROLE_CUSTOMER.value) && userId != null) {
                if (orderDTO.userId != userId.toLong())
                    return ResponseEntity.status(401).body(Values.UNAUTHORIZED)
                val responseOrderDTO = orderService.createOrder(orderDTO)
                return ResponseEntity.ok(responseOrderDTO)
            }
            else
                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

//    @PatchMapping("/{orderId}")
//    fun updateOrder(
//        @PathVariable("orderId") orderId: Long,
//        @RequestBody updateOrderRequestDTO: UpdateOrderRequestDTO,
//        @RequestHeader("userId") userId: String?,
//        @RequestHeader("roles") roles: String?
//    ): ResponseEntity<Any> {
//        try {
//            if (!orderDTO.validatePatch())
//                return ResponseEntity.badRequest().body(Values.INVALID_ORDER_REPRESENTATION)
//            if (roles == null)
//                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
//            if (roles.contains(RoleName.ROLE_ADMIN.value)) {
//                val responseOrderDTO = orderService.updateOrder(orderId, orderDTO)
//                return ResponseEntity.ok(responseOrderDTO)
//            }
//            else if (roles.contains(RoleName.ROLE_CUSTOMER.value) && userId != null) {
//                if (orderDTO.userId != userId.toLong())
//                    return ResponseEntity.status(401).body(Values.UNAUTHORIZED)
//                val responseOrderDTO = orderService.updateOrder(orderId, orderDTO)
//                return ResponseEntity.ok(responseOrderDTO)
//            }
//            else
//                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
//        } catch (e: RuntimeException) {
//            return ResponseEntity.badRequest().body(e.message)
//        }
//    }


    @PatchMapping("/{orderId}")
    fun updateOrder(
        @PathVariable("orderId") orderId: Long,
        @RequestBody updateOrderRequestDTO: UpdateOrderRequestDTO,
        @RequestHeader("userId") userId: String?,
        @RequestHeader("roles") roles: String?
    ): ResponseEntity<Any> {
        try {
            if (!updateOrderRequestDTO.validatePatch())
                return ResponseEntity.badRequest().body(Values.INVALID_ORDER_REPRESENTATION)
            if (roles == null || userId == null)
                return ResponseEntity.badRequest().body(Values.FAILED_TO_AUTHORIZE)
            if (roles.contains(RoleName.ROLE_ADMIN.value)) {
                val responseOrderDTO = orderService.updateOrder(userId, orderId, updateOrderRequestDTO, true)
                return ResponseEntity.ok(responseOrderDTO)
            }
            else if (roles.contains(RoleName.ROLE_CUSTOMER.value)) {
                val responseOrderDTO = orderService.updateOrder(userId, orderId, updateOrderRequestDTO, false)
                return ResponseEntity.ok(responseOrderDTO)
            }
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Values.FAILED_TO_AUTHORIZE)
        } catch (e: RuntimeException) {
            if(e.message == Values.UNAUTHORIZED)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Values.FAILED_TO_AUTHORIZE)
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/{orderId}")
    fun deleteOrder(
        @PathVariable("orderId") orderId: Long,
    ): ResponseEntity<Any> {
        try {
            orderService.deleteOrder(orderId)
            return ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

}