package it.polito.wa2.warehouseservice.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

data class ResponseProductDTO (
    val id: Long?,
    val name: String?,
    var category: String?,
    var description: String?,
    var price: BigDecimal?,
    val pictureUrl: String?,
    val rating: BigDecimal?,
    val creationDate: LocalDateTime?,
    val commentsUrl: String?,
    var totalProductQty: Long? = null
)