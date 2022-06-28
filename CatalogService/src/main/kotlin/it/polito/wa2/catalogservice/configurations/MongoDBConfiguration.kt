package it.polito.wa2.catalogservice.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class MongoDBConfiguration(

    @Value("\${spring.data.mongodb.host}")
    val host : String = ""
)
