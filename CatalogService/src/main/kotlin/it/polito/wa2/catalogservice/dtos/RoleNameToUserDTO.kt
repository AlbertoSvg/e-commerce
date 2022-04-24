package it.polito.wa2.catalogservice.dtos

import javax.validation.constraints.NotBlank

data class RoleNameToUserDTO(
    @field:NotBlank val username : String,
    @field:NotBlank  val role : String
)