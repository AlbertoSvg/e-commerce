package it.polito.wa2.catalogservice.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class JwtConfiguration(

    @Value("\${application.jwt.jwtSecret}")
    val secret: String = "",

    @Value("\${application.jwt.jwtExpirationMs}")
    val expirationMs: Int,

    @Value("\${application.jwt.jwtHeader}")
    val header: String = "",

    @Value("\${application.jwt.jwtHeaderStart}")
    val headerStart: String = ""
)
