package it.polito.wa2.catalogservice.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import it.polito.wa2.catalogservice.enum.RoleName
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsDTO(
    private val id: String,
    private val username: String?,
    @JsonIgnore private val password: String? = null,
    private val email: String? = null,
    private val isEnabled: Boolean? = null,
    private val isAccountNonExpired: Boolean? = null,
    private val isAccountNonLocked: Boolean? = null,
    private val isCredentialsNonExpired: Boolean? = null,
    roles: Set<RoleName>,
    val name: String? = null,
    val surname: String? = null,
    val address: String? = null,
) : UserDetails {

    private val roleSet = roles
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        this.roleSet.forEach { it -> authorities.add(SimpleGrantedAuthority(it.name)) }
        return authorities
    }

    fun getId(): String {
        return id;
    }

    fun getEmail(): String {
        if (email != null)
            return email
        return ""
    }

    fun getRoles(): String {
        return roleSet.joinToString(",")
    }

    override fun getPassword(): String {
        if (password != null)
            return password
        return ""
    }

    override fun getUsername(): String {
        if (username != null)
            return username
        return ""
    }

    override fun isAccountNonExpired(): Boolean {
        if (isAccountNonExpired != null)
            return isAccountNonExpired
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        if (isAccountNonLocked != null)
            return isAccountNonLocked
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        if (isCredentialsNonExpired != null)
            return isCredentialsNonExpired
        return true
    }

    override fun isEnabled(): Boolean {
        if (isEnabled != null)
            return isEnabled
        return false
    }

    override fun toString(): String {
        return "- username: $username - roles: $roleSet\n- isEnabled: $isEnabled - password:$password"
    }

}
