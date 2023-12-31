package it.polito.wa2.orderservice.entities

import it.polito.wa2.orderservice.dtos.OrderItemDTO
import it.polito.wa2.orderservice.dtos.order.request.PurchaseProductDTO
import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.*
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["order_id", "product_id"])])
class OrderItem : EntityBase<Long>() {

    var id = getId()

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "order_id",
        nullable = false,
        updatable = false)
    var order: Order? = null

    @Column(
        name="product_id",
        nullable = false,
        updatable = true
    )
    var productId: Long? = null

    @Column(
        name="amount",
        nullable = false,
        updatable = true
    )
    var amount: Long? = null

    @DecimalMin("0.00", inclusive = true)
    @Digits(fraction = 2, integer = 10)
    @Column(
        name = "price",
        nullable = false,
        updatable = true
    )
    var price: BigDecimal? = null
        set(value) {
            field = value?.setScale(2, RoundingMode.HALF_EVEN)
        }

    @Column(
        name="warehouse_id",
        nullable = true,
        updatable = true
    )
    var warehouseId: Long? = null

    fun toOrderItemDTO(): OrderItemDTO =
        OrderItemDTO(
            //id = id,
            //orderId = order?.id,
            productId = productId,
            amount = amount,
            price = price,
            warehouseId = warehouseId
        )

    fun toPurchaseProductDTO(): PurchaseProductDTO =
        PurchaseProductDTO(
            productId = productId!!,
            amount = amount!!,
            price = price!!
        )
}