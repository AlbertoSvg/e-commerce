package it.polito.wa2.catalogservice.dtos

import javax.validation.constraints.NotBlank

data class RegisterDTO(

    @field:NotBlank(message = "Username required") val username: String,
    @field:NotBlank(message = "Email required") val email: String,
    @field:NotBlank(message = "Name required") val name: String,
    @field:NotBlank(message = "Surname required") val surname: String,
    @field:NotBlank(message = "Address required") val address: String,
    @field:NotBlank(message = "Password required") val password: String,
    @field:NotBlank(message = "Please, confirm password") val confirmPassword: String

)
