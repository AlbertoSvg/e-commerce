package it.polito.wa2.orderservice.entities

import it.polito.wa2.orderservice.constants.OrderStatus
import it.polito.wa2.orderservice.dtos.OrderDTO
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "orders")
class Order : EntityBase<Long>() {

    var id = getId()

    @Column(
        name = "user_id",
        nullable = false,
        updatable = true
    )
    var userId: Long? = null

    @Column(
        name = "wallet_id",
        nullable = false,
        updatable = true
    )
    var walletId: Long? = null

    @Column(
        name = "address",
        nullable = false,
        updatable = true
    )
    var deliveryAddress: String? = null

    @Column(
        name = "order_status",
        nullable = false,
        updatable = true
    )
    var orderStatus: OrderStatus? = null

    @OneToMany(cascade = [CascadeType.REMOVE, CascadeType.PERSIST], mappedBy = "order")
    val items = mutableSetOf<OrderItem>()

    fun toOrderDTO(): OrderDTO =
        OrderDTO(
            id = id,
            userId = userId,
            walletId = walletId,
            deliveryAddress = deliveryAddress,
            status = orderStatus,
            items = items.map { item -> item.toOrderItemDTO() }
        )

    fun updateStatus(newStatus: OrderStatus, e:Exception?=null){
        val exception = e ?: RuntimeException( "Cannot update status from $orderStatus to $newStatus")

        when(newStatus){
            OrderStatus.PENDING -> {
                throw exception
            }
            OrderStatus.ISSUED -> if(orderStatus != OrderStatus.PENDING) throw exception
            OrderStatus.DELIVERING -> if(orderStatus != OrderStatus.ISSUED) throw exception
            OrderStatus.DELIVERED -> if(orderStatus != OrderStatus.DELIVERING) throw exception
            OrderStatus.FAILED -> if(orderStatus != OrderStatus.PENDING) throw exception
            OrderStatus.CANCELED -> if(orderStatus != OrderStatus.ISSUED) throw exception
        }

        orderStatus = newStatus
    }
}

