package it.polito.wa2.warehouseservice.dtos

import java.util.*

data class CommentDTO(
    val title: String,
    val body: String,
    val stars: Int,
    val creationDate: Date,
    val productId: Long,
    val authorUsername: String
)