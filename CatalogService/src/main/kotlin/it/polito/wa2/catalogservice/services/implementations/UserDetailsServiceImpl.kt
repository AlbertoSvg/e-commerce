package it.polito.wa2.catalogservice.services.implementations

import it.polito.wa2.catalogservice.costants.Strings.TOKEN_EXPIRED
import it.polito.wa2.catalogservice.costants.Strings.TOKEN_NOT_FOUND
import it.polito.wa2.catalogservice.costants.Strings.USER_NOT_FOUND
import it.polito.wa2.catalogservice.costants.Strings.WRONG_PARAMETERS
import it.polito.wa2.catalogservice.entities.User
import it.polito.wa2.catalogservice.entities.toUserDTO
import it.polito.wa2.catalogservice.enum.RoleName
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import it.polito.wa2.catalogservice.services.interfaces.MailService
import it.polito.wa2.catalogservice.services.interfaces.NotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import it.polito.wa2.catalogservice.dtos.UserDetailsDTO as UserDetailsDTO1


@Service
@Transactional
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var notificationService: NotificationService

    @Autowired
    private lateinit var mailService: MailService

    @Autowired
    private lateinit var emailVerificationTokenRepository: EmailVerificationTokenRepository

    override fun loadUserByUsername(username: String?): UserDetails? {
        var user: User? = null
        if (username != null) user = userRepository.findByUsername(username)
        if (user == null) throw UsernameNotFoundException(USER_NOT_FOUND)
        return user.toUserDTO()
    }

    fun usernameExists(username: String?): Boolean {
        var user: User? = null
        if (username != null) user = userRepository.findByUsername(username)
        return user != null
    }

    fun emailExists(email: String?): Boolean {
        var user: User? = null
        if (email != null) user = userRepository.findByEmail(email)
        return user != null
    }

    fun getUserByEmail(email: String): UserDetails? {
        val user = userRepository.findByEmail(email) ?: throw RuntimeException(USER_NOT_FOUND)
        return user.toUserDTO()
    }

    fun registerUser(
        username: String,
        password: String,
        email: String,
        isEnabled: Boolean,
        roles: String,
        name: String,
        surname: String,
        address: String
    ): UserDetailsDTO1 {
        try {
            val user = User().apply {
                this.username = username
                this.password = password
                this.isEnabled = isEnabled
                this.email = email
                this.name = name
                this.surname = surname
                this.address = address
                roles.split(" ").map { it -> RoleName.valueOf(it) }.forEach { addRoleName(it) }
            }

            val dto = userRepository.save(user).toUserDTO()
            val token = notificationService.createEmailVerificationToken(user.username)
            val mailBody: String = "Click here to confirm the registration" + "\r\n" +
                    "http://localhost:8080/auth/registrationConfirm?token=" + token

            mailService.sendMessage(
                user.email,
                "Registration email",
                mailBody
            )
            return dto
        } catch (e: RuntimeException) {
            throw RuntimeException(e.message)
        }
    }

    fun confirmUserRegistration(token: String) {
        if (token != "") {
            val verificationToken = emailVerificationTokenRepository.findByToken(token)
                ?: throw BadCredentialsException(TOKEN_NOT_FOUND)
            val now = LocalDateTime.now()
            if (verificationToken.expiryDate.isBefore(now)) {
                throw BadCredentialsException(TOKEN_EXPIRED)
            } else {
                verificationToken.user.isEnabled = true
                emailVerificationTokenRepository.deleteById(verificationToken.id!!)
            }
        } else throw BadCredentialsException(WRONG_PARAMETERS)
    }

    fun addRoleToUser(username: String?, role: RoleName): UserDetailsDTO1 {
        try {
            var user: User? = null

            if (username != null) user = userRepository.findByUsername(username)
            if (user == null) throw UsernameNotFoundException(USER_NOT_FOUND)
            user.addRoleName(role)
            return userRepository.save(user).toUserDTO()

        } catch (e: RuntimeException) {
            throw RuntimeException(e.message)
        }
    }

    fun removeRoleFromUser(username: String?, role: RoleName): UserDetailsDTO1 {
        try {
            var user: User? = null

            if (username != null) user = userRepository.findByUsername(username)
            if (user == null) throw UsernameNotFoundException(USER_NOT_FOUND)
            user.removeRoleName(role)
            return userRepository.save(user).toUserDTO()

        } catch (e: RuntimeException) {
            throw RuntimeException(e.message)
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    fun enableUser(username: String, enable: Boolean ): UserDetailsDTO1 {
        try {
            var user: User? = null

            user = userRepository.findByUsername(username)
            if (user == null) throw UsernameNotFoundException(USER_NOT_FOUND)
            user.isEnabled = enable
            return userRepository.save(user).toUserDTO()

        } catch (e: RuntimeException) {
            throw RuntimeException(e.message)
        }
    }

    fun isEnabled(username: String?): Boolean {
        try {
            var user: User? = null
            if (username != null) user = userRepository.findByUsername(username)
            if (user == null) throw UsernameNotFoundException(USER_NOT_FOUND)
            return user.isEnabled
        } catch (e: RuntimeException) {
            throw RuntimeException(e.message)
        }
    }
}
