package it.polito.wa2.catalogservice.entities

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.validation.constraints.NotNull


@Document(collection = "email_verification_token")
class EmailVerificationToken(
    val expiryDate: String = LocalDateTime.now().plusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
){
    @Id
    @field:NotNull
    var id: String? = null

    @field:NotNull
    @Indexed(unique=true)
    lateinit var token: String

    @field:NotNull
    lateinit var userId: String

}

