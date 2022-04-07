package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.ProductStockDTO
import javax.persistence.*

@Entity
@Table(name = "product_stock")
class ProductStock : EntityBase<Long>() {

    var id = getId()

    @ManyToOne
    @JoinColumn(name = "warehouse")
    var warehouse: Warehouse? = null

    @ManyToOne
    @JoinColumn(name = "product")
    var product: Product? = null

    @Column(name = "product_qty")
    var productQty: Long? = null

    @Column(name = "alarm")
    var alarmLevel: Long? = null

    fun toProductStockDTO(): ProductStockDTO =
        ProductStockDTO(
            id = id,
            warehouseId = warehouse?.id!!,
            productId = product?.id!!,
            productQty = productQty!!,
            alarmLevel = alarmLevel!!
        )
}

