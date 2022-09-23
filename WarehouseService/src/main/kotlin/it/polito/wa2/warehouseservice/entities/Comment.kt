package it.polito.wa2.warehouseservice.entities

import it.polito.wa2.warehouseservice.dtos.CommentDTO
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Entity
@Table(name = "comment")
class Comment : EntityBase<Long>(){
    var id = getId()

    @Column(name = "title")
    var title: String? = null

    @Column(name = "body")
    var body: String? = null

    @Column(name = "stars")
    @Min(value = 0, message =  "Quantity must be higher than 0")
    @Max(value = 5, message = "Quantity must be lower than 5")
    var stars: Int? = null

    @Column(name= "user")
    var userId: Long? = null

    @ManyToOne
    @JoinColumn(name = "product",  referencedColumnName = "id")
    var product: Product? = null

    fun toCommentDTO(): CommentDTO =
        CommentDTO(
            title = title!!,
            body = body!!,
            stars = stars!!,
            productId = product!!.id!!,
            userId = userId!!,
            creationDate = Date()
        )
}