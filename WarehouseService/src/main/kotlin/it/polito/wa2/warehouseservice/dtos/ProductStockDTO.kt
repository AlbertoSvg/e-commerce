package it.polito.wa2.warehouseservice.dtos

data class ProductStockDTO (
    val productId: Long?,
    val productQty: Long?,
    val alarmLevel: Long?
)