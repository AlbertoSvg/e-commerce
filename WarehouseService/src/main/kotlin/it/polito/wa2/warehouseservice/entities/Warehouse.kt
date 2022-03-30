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

    //TODO: testa se la relazione è corretta da ambo i lati
    @OneToMany(mappedBy = "warehouse")
    val productsStocks = mutableSetOf<ProductStock>()
}

fun Warehouse.toWarehouseDTO(): WarehouseDTO =
    WarehouseDTO(
        id = id,
        name = name!!
    )