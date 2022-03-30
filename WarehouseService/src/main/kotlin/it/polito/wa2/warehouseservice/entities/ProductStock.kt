package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.ProductStockDTO
import javax.persistence.*

@Entity
@Table(name = "product_stock")
class ProductStock : EntityBase<Long>() {

    var id = getId()

    @ManyToOne
    var warehouse: Warehouse? = null

    @Column(name = "product_id")
    var productId: Long? = null

    @Column(name = "product_qty")
    var productQty: Long? = null

    @Column(name = "alarm")
    var alarmLevel: Long? = null
}

fun ProductStock.toProductStockDTO(): ProductStockDTO =
    ProductStockDTO(
        id = id,
        warehouseId = warehouse?.id!!,
        productId = productId!!,
        productQty = productQty!!,
        alarmLevel = alarmLevel!!
    )