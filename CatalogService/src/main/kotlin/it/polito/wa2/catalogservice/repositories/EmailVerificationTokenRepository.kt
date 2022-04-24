package it.polito.wa2.catalogservice.repositories

//import it.polito.wa2.catalogservice.entities.EmailVerificationToken
import it.polito.wa2.catalogservice.entities.EmailVerificationToken
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface EmailVerificationTokenRepository : ReactiveMongoRepository<EmailVerificationToken, String> {

    /** ### Description:
     * Find a token in the database
     * @param token The UUID token string (String)
     * @return An EmailVerificationToken?.
     */
    fun findByToken(token: String): Mono<EmailVerificationToken?>

    /** ### Description:
     * Delete all expired tokens whose expiration date is earlier than the current time
     * @param now The current time (LocalDateTime)
     */

    @Query(value = "{'expiryDate' : { \$lte: ?0 } }", delete = true)
    fun deleteAllByExpiryDateBefore(now: String) : Mono<Int>

    override fun deleteById(id: String) : Mono<Void>
    /** ### Description:
     * Find all the EmailVerificationToken whose expiration date is earlier than the current time
     * @param now The current time (LocalDateTime)
     * @return A MutableIterable of EmailVerificationToken.
     */

    @Query("{'expiryDate' : { \$lte: ?0 } }")
    fun findAllByExpiryDateBefore(now: String): Flux<EmailVerificationToken>

}
