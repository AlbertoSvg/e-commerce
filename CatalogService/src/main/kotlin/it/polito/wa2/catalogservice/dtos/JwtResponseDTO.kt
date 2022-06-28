package it.polito.wa2.catalogservice.dtos

data class JwtResponseDTO(

    val id: String,
    val username: String,
    val email: String,
    val jwt: String,
    val role: Set<String>,

    )
