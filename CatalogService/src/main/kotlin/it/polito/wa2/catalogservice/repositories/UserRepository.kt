package it.polito.wa2.catalogservice.repositories

import it.polito.wa2.catalogservice.entities.User
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Repository
interface UserRepository : ReactiveMongoRepository<User, Long> {

    /** ### Description:
     * Find a user using his username
     * @param username The username of the user (String)
     * @return A User? object
     */
    fun findByUsername(username: String): Mono<User?>

    /** ### Description:
     * Find a user using his email address
     * @param email The email address of the user (String)
     * @return A User? object
     */
    fun findByEmail(email: String): Mono<User?>

    /** ### Description:
     * Find a user using the ID number
     * @param id The id of the user (Long)
     * @return A container object (of User) which may or may not contain a non-null value.
     */
    override fun findById(id: Long): Mono<User?>

    override fun deleteById(id: Long): Mono<Void>
}
