package it.polito.wa2.warehouseservice.dtos

data class ProductDTO (
    val id: Long?,
    var category: String?,
    var description: String?,
    var price: Float?,
    val pictureUrl: String?
)