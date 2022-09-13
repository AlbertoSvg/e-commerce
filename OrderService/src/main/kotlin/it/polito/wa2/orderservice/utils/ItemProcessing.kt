package it.polito.wa2.orderservice.utils

import it.polito.wa2.orderservice.dtos.order.request.ProductsInWarehouseDTO
import it.polito.wa2.orderservice.dtos.order.request.PurchaseProductDTO
import it.polito.wa2.orderservice.dtos.order.request.PurchasedProductDTO
import it.polito.wa2.orderservice.entities.OrderItem

fun Set<OrderItem>.extractProductInWarehouse(mapper: (OrderItem)-> PurchasedProductDTO): List<ProductsInWarehouseDTO>{
    return this.groupBy ({ it.warehouseId }, mapper)
        .map {
            ProductsInWarehouseDTO(
                it.key,
                it.value
            ) }
}