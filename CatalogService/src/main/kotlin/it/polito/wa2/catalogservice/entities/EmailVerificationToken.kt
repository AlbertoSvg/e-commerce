package it.polito.wa2.catalogservice.entities

import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "email_verification_token")
class EmailVerificationToken : EntityBase<Long>() {

    var id = getId()

    @Column(name = "token", nullable = false, unique = true)
    lateinit var token: String

    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    lateinit var user: User

    @Column(
        name = "timestamp",
        nullable = false,
        columnDefinition = "TIMESTAMP",
        updatable = false
    )
    lateinit var expiryDate: LocalDateTime

    @PrePersist
    fun prePersistCreatedAt() {
        this.expiryDate = LocalDateTime.now().plusHours(24)
    }

}
