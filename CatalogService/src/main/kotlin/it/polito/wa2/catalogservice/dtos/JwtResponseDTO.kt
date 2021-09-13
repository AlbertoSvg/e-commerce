package it.polito.wa2.catalogservice.dtos

data class JwtResponseDTO(

    val username: String,
    val email: String,
    val customerId: Long,
    val jwt: String,
    val role: Set<String>,

    )
