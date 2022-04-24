package it.polito.wa2.catalogservice.dtos

import jdk.jfr.BooleanFlag
import javax.validation.constraints.NotBlank

data class EnableUserDTO(
    @field:NotBlank val username: String,
    @field:BooleanFlag val enable: Boolean
)