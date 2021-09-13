package it.polito.wa2.catalogservice.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import it.polito.wa2.catalogservice.enum.RoleName
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class UserDetailsDTO(
    private val username: String?,
    @JsonIgnore private val password: String?,
    private val name: String?,
    private val surname: String?,
    private val address: String?,
    private val email: String?,
    private val isEnabled: Boolean?,
    private val isAccountNonExpired: Boolean?,
    private val isAccountNonLocked: Boolean?,
    private val isCredentialsNonExpired: Boolean?,
    private val userId: Long?, //ho sostituito customerId con userId
    roles: Set<RoleName>?
) : UserDetails {

    private val roleSet = roles
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        this.roleSet?.forEach { it -> authorities.add(SimpleGrantedAuthority(it.value)) }
        return authorities
    }

    fun getEmail(): String {
        if (email != null)
            return email
        return ""
    }

//    fun getCustomerId(): Long {
//        if (customerId != null)
//            return customerId
//        return -1L
//    }

    fun getUserId() :Long {
        if(userId != null)
            return userId
        return -1L
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
