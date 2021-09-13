package it.polito.wa2.catalogservice.repositories

import it.polito.wa2.catalogservice.entities.EmailVerificationToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface EmailVerificationTokenRepository : CrudRepository<EmailVerificationToken, Long> {

    /** ### Description:
     * Find a token in the database
     * @param token The UUID token string (String)
     * @return An EmailVerificationToken?.
     */
    fun findByToken(token: String): EmailVerificationToken?

    /** ### Description:
     * Delete all expired tokens whose expiration date is earlier than the current time
     * @param now The current time (LocalDateTime)
     */
    fun deleteAllByExpiryDateBefore(now: LocalDateTime)

    /** ### Description:
     * Find all the EmailVerificationToken whose expiration date is earlier than the current time
     * @param now The current time (LocalDateTime)
     * @return A MutableIterable of EmailVerificationToken.
     */
    fun findAllByExpiryDateBefore(now: LocalDateTime): MutableIterable<EmailVerificationToken>

}
