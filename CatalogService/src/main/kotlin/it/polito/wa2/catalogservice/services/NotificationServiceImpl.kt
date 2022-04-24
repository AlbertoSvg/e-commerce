package it.polito.wa2.catalogservice.services

//import it.polito.wa2.catalogservice.entities.EmailVerificationToken
import it.polito.wa2.catalogservice.constants.Strings.FAILED_TO_SAVE_OBJECT
import it.polito.wa2.catalogservice.constants.Strings.USER_NOT_FOUND
import it.polito.wa2.catalogservice.entities.EmailVerificationToken
import it.polito.wa2.catalogservice.entities.User
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
//import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Service
@Transactional
class NotificationServiceImpl : NotificationService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var emailVerificationTokenRepository: EmailVerificationTokenRepository

    override fun createEmailVerificationToken(userName: String): Mono<String> {
        val verificationToken = EmailVerificationToken()
        val user = userRepository.findByUsername(userName)

        return user.flatMap {
            if (it != null) {
                verificationToken.userId = it.id!!
                verificationToken.token = UUID.randomUUID().toString()
                return@flatMap emailVerificationTokenRepository.save(verificationToken)
                    .map { e -> e.token }.switchIfEmpty { Mono.error(RuntimeException(FAILED_TO_SAVE_OBJECT)) }
            } else
                Mono.error(UsernameNotFoundException(USER_NOT_FOUND))
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun removeExpiredTokens() {

        CoroutineScope(Dispatchers.IO).launch {
            removeTokensAndUsers()
        }
    }

    suspend fun removeTokensAndUsers(){
        var user: User?
        val usersToDelete = mutableListOf<String>()
        val currentTime = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))

        // find all expired tokens
        val expiredTokensFlux = emailVerificationTokenRepository.findAllByExpiryDateBefore(currentTime)
        val expiredTokens = expiredTokensFlux.collectList().awaitSingle()
        expiredTokens.forEach { evt ->
                            user = userRepository.findById(evt.userId).awaitSingle()
                            if (user != null) {
                                if (!user!!.isEnabled) {
                                    usersToDelete.add(user!!.id!!)
                                }
                            }
                        }

        // finally delete all expired tokens...
        val n =emailVerificationTokenRepository.deleteAllByExpiryDateBefore(currentTime).awaitSingle()
        if(n>0)
            println("$n tokens removed")
        // ...and users
        usersToDelete.forEach { userRepository.deleteById(it)
            .onErrorMap { RuntimeException(FAILED_TO_SAVE_OBJECT) }
            .doOnSuccess { println("user removed") }
            .subscribe()}
    }
}
