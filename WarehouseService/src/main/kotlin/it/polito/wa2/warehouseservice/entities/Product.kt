package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.ProductDTO
import javax.persistence.*

//NOTA: se aggiungiamo campi all'entità, modifcare DTO, service, controller, validators per coerenza

@Entity
@Table(name = "product")
class Product : EntityBase<Long>(){

    var id = getId()

    @Column(
        name = "category",
        nullable = false,
        updatable = true
    )
    var category: String? = null

    @Column(
        name = "description",
        nullable = false,
        updatable = true
    )
    var description: String? = null

    @Column(
        name = "price",
        nullable = false,
        updatable = true
    )
    var price: Float? = null

    @Lob
    @Column(
        name="picture",
        updatable = true
    )
    var picture: ByteArray? = null

    @OneToMany(mappedBy = "product")
    val productsStocks = mutableSetOf<ProductStock>()

    fun toProductDTO() : ProductDTO =
        ProductDTO(
            id = id,
            category = category,
            description = description,
            price = price,
            pictureUrl = "/products/$id/picture"
        )
}