package it.polito.wa2.catalogservice.services.implementations

import it.polito.wa2.catalogservice.costants.Strings.USER_NOT_FOUND
import it.polito.wa2.catalogservice.entities.EmailVerificationToken
import it.polito.wa2.catalogservice.entities.User
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import it.polito.wa2.catalogservice.services.interfaces.NotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*


@Service
@Transactional
class NotificationServiceImpl : NotificationService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var emailVerificationTokenRepository: EmailVerificationTokenRepository

    override fun createEmailVerificationToken(userName: String): String {
        val verificationToken = EmailVerificationToken()
        val user = userRepository.findByUsername(userName) ?: throw RuntimeException(USER_NOT_FOUND)
        verificationToken.user = user
        verificationToken.token = UUID.randomUUID().toString()
        emailVerificationTokenRepository.save(verificationToken)
        return verificationToken.token
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24) //cleanup every 24 hours
    fun removeExpiredTokens() {
        var user: Optional<User>
        val currentTime = LocalDateTime.now()
        // find all expired tokens
        val expiredTokens = emailVerificationTokenRepository.findAllByExpiryDateBefore(currentTime)
        val usersToDelete = mutableListOf<Long>()

        // for each expired token, add corresponding (via user id) user to a to-be-removed list, given they are not enabled yet
        expiredTokens.forEach {
            user = userRepository.findById(it.user.id!!)
            if (user.isPresent && !user.get().isEnabled) usersToDelete.add(user.get().id!!)
        }
        // finally delete all expired tokens...
        emailVerificationTokenRepository.deleteAllByExpiryDateBefore(currentTime)
        // ...and users
        usersToDelete.forEach { userRepository.deleteById(it) }
    }
}
