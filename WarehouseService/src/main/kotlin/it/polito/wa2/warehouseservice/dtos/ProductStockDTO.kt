package it.polito.wa2.warehouseservice.dtos

data class ProductStockDTO (
    val id: Long?,
    val warehouseId: Long,
    val productId: Long,
    val productQty: Long,
    val alarmLevel: Long
)