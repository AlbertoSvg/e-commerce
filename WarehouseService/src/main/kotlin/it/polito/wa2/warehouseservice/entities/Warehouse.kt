package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.WarehouseDTO
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "warehouse")
class Warehouse : EntityBase<Long>() {

    var id = getId()

    @Column(
        name="name",
        nullable = false,
        updatable = true
    )
    var name: String? = null


    @OneToMany(mappedBy = "warehouse")
    val productsStocks = mutableSetOf<ProductStock>()

    fun toWarehouseDTO(): WarehouseDTO =
        WarehouseDTO(
            id = id,
            name = name
        )
}

