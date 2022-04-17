package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.ProductStockDTO
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
@Table(name = "product_stock")
class ProductStock: EntityBase<Long>() {

    var id = getId()

    @ManyToOne
    @JoinColumn(name = "warehouse")
    var warehouse: Warehouse? = null

    @ManyToOne
    @JoinColumn(name = "product")
    var product: Product? = null

    @Column(name = "product_qty")
    @Min(value = 0, message =  "Quantity must be positive or zero")
    var productQty: Long? = null

    @Column(name = "alarm")
    @Min(value = 0, message =  "Alarm level must be positive or zero")
    var alarmLevel: Long? = null

    fun toProductStockDTO(): ProductStockDTO =
        ProductStockDTO(
            productId = product?.id!!,
            productQty = productQty!!,
            alarmLevel = alarmLevel!!
        )
}

