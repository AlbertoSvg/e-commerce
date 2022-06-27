package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.ResponseProductDTO
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.*

//NOTA: se aggiungiamo campi all'entità, modifcare DTO, service, controller, validators per coerenza

@Entity
@Table(name = "product")
class Product : EntityBase<Long>(){

    var id = getId()

    @Column(
        name = "name",
        nullable = false,
        updatable = true
    )
    var name: String? = null

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

    @Lob
    @Column(
        name="picture",
        updatable = true
    )
    var picture: ByteArray? = null

    @Min(0)
    @Column(
        name = "num_stars",
        nullable = false
    )
    var numStars: Long? = null

    @Min(0)
    @Column(
        name = "num_ratings",
        nullable = false,
    )
    var numRatings: Long? = null

    @Column(
        name = "creation_date",
        nullable = false,
        columnDefinition = "TIMESTAMP(3)",
        updatable = false
    )
    var creationDate: LocalDateTime? = null

    @PrePersist
    fun prePersistCreatedAt() {
        this.creationDate = LocalDateTime.now()
    }

    @OneToMany(mappedBy = "product")
    val productsStocks = mutableSetOf<ProductStock>() //todo: Ci serve davvero?
    fun toProductDTO() : ResponseProductDTO {
        val rating =
            if(numRatings == 0L)
                BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_EVEN)
            else BigDecimal
                .valueOf(numStars!!.toDouble()/numRatings!!.toDouble())
                .setScale(2, RoundingMode.HALF_EVEN)
        return ResponseProductDTO(
            id = id,
            name = name,
            category = category,
            description = description,
            price = price,
            pictureUrl = "/products/$id/picture",
            rating,
            creationDate,
            "/products/$id/comments"
        )
    }
}