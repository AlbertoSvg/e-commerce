package it.polito.wa2.warehouseservice.dtos

import java.math.BigDecimal

data class ProductDTO(
    val name: String? = null,
    val description: String? = null,
    val category: String? = null,
    val price: BigDecimal? = null
)