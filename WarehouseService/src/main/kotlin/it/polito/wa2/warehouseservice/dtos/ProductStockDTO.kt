package it.polito.wa2.warehouseservice.dtos

data class ProductStockDTO (
    val warehouseId: Long? = null,
    val productId: Long?,
    val productQty: Long?,
    val alarmLevel: Long?
)