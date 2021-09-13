package it.polito.wa2.catalogservice.dtos

import javax.validation.constraints.NotBlank

data class LoginDTO(

    @field:NotBlank(message = "Username required") val username: String,
    @field:NotBlank(message = "Password required") val password: String

)
