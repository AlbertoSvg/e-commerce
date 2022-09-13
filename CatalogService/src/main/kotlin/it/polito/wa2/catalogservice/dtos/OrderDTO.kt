package it.polito.wa2.catalogservice.dtos

import org.jetbrains.annotations.NotNull
import javax.validation.constraints.Size

data class OrderDTO (
    val walletId: Long,
    val deliveryAddress: String,
    val items: List<ItemDTO>
)