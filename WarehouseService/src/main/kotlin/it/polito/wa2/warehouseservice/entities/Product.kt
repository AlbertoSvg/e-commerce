package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.ResponseProductDTO
import it.polito.wa2.warehouseservice.repositories.ProductStockRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.*


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
    var category: Category? = null

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
        updatable = true,
        nullable = true
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

    fun toProductDTO(
    ) : ResponseProductDTO {
        val rating =
            if(numRatings == 0L)
                BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_EVEN)
            else BigDecimal
                .valueOf(numStars!!.toDouble()/numRatings!!.toDouble())
                .setScale(2, RoundingMode.HALF_EVEN)

        return ResponseProductDTO(
            id = id,
            name = name,
            category = category?.name,
            description = description,
            price = price,
            pictureUrl = (if (picture != null) "/products/$id/picture" else ""),
            rating,
            creationDate,
            "/products/$id/comments"
        )
    }
}
enum class Category {
    HOME, FOOD, TECH, GAMES, CLOTHES, MUSIC, BOOKS, OTHER
}