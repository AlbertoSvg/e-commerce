package it.polito.wa2.catalogservice.dtos

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class RegisterDTO(

    @field:NotBlank(message = "Username required")
    val username: String,
    @field:NotBlank(message = "Email required")
    val email: String,
    @field:NotBlank(message = "Name required")
    val name: String,
    @field:NotBlank(message = "Surname required")
    val surname: String,
    @field:NotBlank(message = "Address required")
    val address: String,
    @field:NotBlank(message = "Password required")
    @field:Size(min = 8, message = "password should have a minimum of 8 characters")
    val password: String,
    @field:NotBlank(message = "Please, confirm password")
    @field:Size(min = 8, message = "password should have a minimum of 8 characters")
    val confirmPassword: String

)
