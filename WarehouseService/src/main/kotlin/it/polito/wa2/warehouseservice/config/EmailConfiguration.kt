package it.polito.wa2.warehouseservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class EmailConfiguration(

    @Value("\${spring.mail.host}")
    val host: String = "",

    @Value("\${spring.mail.port}")
    val port: Int = 0,

    @Value("\${spring.mail.username}")
    val username: String = "",

    @Value("\${spring.mail.password}")
    val password: String = ""

)