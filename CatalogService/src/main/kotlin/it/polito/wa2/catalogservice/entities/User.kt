package it.polito.wa2.catalogservice.entities

import it.polito.wa2.catalogservice.dtos.UserDetailsDTO
import it.polito.wa2.catalogservice.enum.RoleName
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

// TODO: Vogliamo inglobare Customer dentro a User?? Dopotutto Customer e' uno User

@Entity
@Table(name = "user", indexes = [Index(name = "index", columnList = "username", unique = true)])
class User : EntityBase<Long>() {

    var id = getId()

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    lateinit var username: String

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    var password: String = ""
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }

    @Column(name = "email", unique = true, nullable = false)
    @NotEmpty(message = "Email is required")
    @Email
    lateinit var email: String

    @Column(name = "is_enabled", nullable = false)
    var isEnabled: Boolean = false

    @Column(name = "roles")
    var roles: String = ""

    // @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], mappedBy = "user")
    // lateinit var customer: Customer

    //prima era in customer
    @Column(
        name = "name",
        nullable = false,
        updatable = true
    )
    var name: String? = null

    //prima era in customer
    @Column(
        name = "surname",
        nullable = false,
        updatable = true
    )
    var surname: String? = null

    //prima era in customer
    @Column(
        name = "address",
        nullable = false,
        updatable = true
    )
    var address: String? = null

    fun getRoleNames(): Set<RoleName> {
        val rolesList = roles.trim().split(" ")
        val rolesListTrimmed = rolesList.map { it -> it.trim() }
        val rolesValues = rolesListTrimmed.map { it -> RoleName.valueOf(it) }
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
    username = username,
    password = password,
    name = name, //da customer
    surname = surname, //da customer
    address = address, //da customer
    email = email,
    isEnabled = isEnabled,
    isAccountNonExpired = null,
    isAccountNonLocked = null,
    isCredentialsNonExpired = null,
    roles = getRoleNames(),
    // customerId = customer.id
)
