package it.polito.wa2.catalogservice.entities

import it.polito.wa2.catalogservice.dtos.UserDetailsDTO
import it.polito.wa2.catalogservice.enum.RoleName
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Document(collection = "user")
class User {

    @Id
    @field:NotNull
    var id: String? = null

    @field:NotBlank(message = "Username is required")
    @field:NotNull
    lateinit var username: String

    @field:NotBlank(message = "Password is required")
    var password: String = ""
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }


    @field:NotEmpty(message = "Email is required")
    @field:Email
    @field:NotNull
    lateinit var email: String

    @field:NotNull(message = "name must be present")
    lateinit var name: String

    @field:NotNull(message = "surname must be present")
    lateinit var surname: String

    var address: String? = null

    @field:NotNull
    var isEnabled: Boolean = false


    @field:NotBlank
    var roles: String = ""


    fun getRoleNames(): Set<RoleName> {
        val rolesList = roles.trim().split(" ")
        val rolesListTrimmed = rolesList.map { it -> it.trim() }
        val rolesValues = rolesListTrimmed.map { it -> RoleName.valueOf("ROLE_$it") }
        return rolesValues.toSet()
    }

    fun addRoleName(role: RoleName) {
        if (!roles.contains(role.value))
            roles = roles.plus("${role.value} ")
    }

    fun removeRoleName(role: RoleName) {
        roles = roles.split(" ").filter { it -> it != role.value }.joinToString(" ")
    }

}

fun User.toUserDTO(): UserDetailsDTO = UserDetailsDTO(
    id = id!!.toString(),
    username = username,
    password = password,
    email = email,
    isEnabled = isEnabled,
    isAccountNonExpired = null,
    isAccountNonLocked = null,
    isCredentialsNonExpired = null,
    roles = getRoleNames(),
    name = name,
    surname = surname,
    address = address
)
