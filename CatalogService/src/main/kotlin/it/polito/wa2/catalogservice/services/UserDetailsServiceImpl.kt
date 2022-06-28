package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.constants.Strings.FAILED_TO_SAVE_OBJECT
import it.polito.wa2.catalogservice.constants.Strings.REGISTRATION_FAILED
import it.polito.wa2.catalogservice.constants.Strings.TOKEN_EXPIRED
import it.polito.wa2.catalogservice.constants.Strings.TOKEN_NOT_FOUND
import it.polito.wa2.catalogservice.constants.Strings.USER_NOT_FOUND
import it.polito.wa2.catalogservice.constants.Strings.WRONG_PARAMETERS
import it.polito.wa2.catalogservice.dtos.UserDetailsDTO
import it.polito.wa2.catalogservice.entities.User
import it.polito.wa2.catalogservice.entities.toUserDTO
import it.polito.wa2.catalogservice.enum.RoleName
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.support.NotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
@Transactional
class UserDetailsServiceImpl : ReactiveUserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var notificationService: NotificationService

    @Autowired
    private lateinit var mailService: MailService

    @Autowired
    private lateinit var emailVerificationTokenRepository: EmailVerificationTokenRepository


    override fun findByUsername(username: String): Mono<UserDetails> {
        val user: Mono<User?> = userRepository.findByUsername(username)
        return user.map { it?.toUserDTO() as UserDetails }
            .switchIfEmpty(Mono.error(UsernameNotFoundException(USER_NOT_FOUND)))
    }

    fun usernameExists(username: String): Mono<Boolean> {
        return userRepository.findByUsername(username).hasElement()
    }

    fun emailExists(email: String): Mono<Boolean> {
        return userRepository.findByEmail(email).hasElement()
    }

    suspend fun registerUser(
        username: String,
        password: String,
        email: String,
        isEnabled: Boolean,
        roles: String,
        name: String,
        surname: String,
        address: String
    ): UserDetailsDTO {
        try {
            val user = User().apply {
                this.username = username
                this.password = password
                this.isEnabled = isEnabled
                this.email = email
                roles.split(" ").map { it -> RoleName.valueOf(it) }.forEach { addRoleName(it) }
                this.name = name
                this.surname = surname
                this.address = address
            }

            val returnUser = withContext(Dispatchers.IO) {
                userRepository.save(user).block()
            } ?: throw RuntimeException(REGISTRATION_FAILED)

            CoroutineScope(Dispatchers.IO).launch {
                sendEmail(returnUser)
            }

            return returnUser.toUserDTO()

        } catch (e: RuntimeException) {
            throw RuntimeException(e.message)
        }
    }

    suspend fun sendEmail(user: User) {
        val token = notificationService.createEmailVerificationToken(user.username).awaitSingle()
        val mailBody: String = "Click here to confirm the registration" + "\r\n" +
                "http://localhost:8080/auth/registrationConfirm?token=" + token

        mailService.sendMessage(
            user.email,
            "Registration email",
            mailBody
        )
    }

    suspend fun confirmUserRegistration(token: String) {
        if (token != "") {
            val verificationToken = emailVerificationTokenRepository.findByToken(token).awaitSingleOrNull()
                ?: throw NotFoundException(TOKEN_NOT_FOUND)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            val now = LocalDateTime.now().format(formatter)
            val dateTimeNow = LocalDateTime.parse(now, formatter)
            val dateTime = LocalDateTime.parse(verificationToken.expiryDate, formatter)
            if (dateTime.isBefore(dateTimeNow))
                throw BadCredentialsException(TOKEN_EXPIRED)
            else {
                val user = userRepository.findById(verificationToken.userId.toLong()).awaitSingle()
                    ?: throw UsernameNotFoundException(USER_NOT_FOUND)
                user.isEnabled = true
                userRepository.save(user).switchIfEmpty(Mono.error(RuntimeException("Enabling User Failed")))
                    .subscribe()
                emailVerificationTokenRepository.deleteById(verificationToken.id!!)
                    .onErrorMap {
                        RuntimeException("Token Cancellation Failed")
                    }.subscribe()

                return
            }
        } else
            throw (BadCredentialsException(WRONG_PARAMETERS))
    }

    suspend fun addRoleToUser(username: String?, role: String) {
        var user: User? = null
        if (username != null) user = userRepository.findByUsername(username).awaitSingleOrNull()
        if (user == null) throw UsernameNotFoundException(USER_NOT_FOUND)
        val roleName = RoleName.valueOf(role)
        user.addRoleName(roleName)
        userRepository.save(user).switchIfEmpty { Mono.error(RuntimeException(FAILED_TO_SAVE_OBJECT)) }.subscribe()
    }

    suspend fun removeRoleFromUser(username: String?, role: String) {
        var user: User? = null
        if (username != null) user = userRepository.findByUsername(username).awaitSingleOrNull()
        if (user == null) throw UsernameNotFoundException(USER_NOT_FOUND)
        val roleName = RoleName.valueOf(role)
        user.removeRoleName(roleName)
        userRepository.save(user).switchIfEmpty { Mono.error(RuntimeException(FAILED_TO_SAVE_OBJECT)) }.subscribe()
    }

    suspend fun enableUser(username: String, enable: Boolean) {
        val user = userRepository.findByUsername(username).awaitSingleOrNull() ?: throw UsernameNotFoundException(
            USER_NOT_FOUND
        )
        user.isEnabled = true
        userRepository.save(user).switchIfEmpty { Mono.error(RuntimeException(FAILED_TO_SAVE_OBJECT)) }.subscribe()
    }

}

