package it.polito.wa2.catalogservice


import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import it.polito.wa2.catalogservice.configurations.EmailConfiguration
import it.polito.wa2.catalogservice.entities.User
import it.polito.wa2.catalogservice.enum.RoleName
import it.polito.wa2.catalogservice.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import java.util.*

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
class CatalogServiceApplication {

    @Autowired
    lateinit var emailCfg: EmailConfiguration

    //Gestire l'invio delle mail con il token di registrazione
    @Bean
    fun getMailSender(): JavaMailSender? {

        val mailSender = JavaMailSenderImpl()
        mailSender.host = emailCfg.host
        mailSender.port = emailCfg.port
        mailSender.username = emailCfg.username
        mailSender.password = emailCfg.password

        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "false"

        return mailSender
    }

    @Bean
    fun createCustomersAndUsers(
        @Autowired userRepository: UserRepository
    ): CommandLineRunner {
        return CommandLineRunner {

            //Clear DB
            var mongoClient = MongoClients.create("mongodb://localhost:27017")
            val database: MongoDatabase = mongoClient.getDatabase("catalog")
            var collection = database.getCollection("user")
            Mono.`when`(collection.drop()).block()

            collection = database.getCollection("email_verification_token")
            Mono.`when`(collection.drop()).block()

            println("Collection dropped successfully");

            val user1 = User() //ADMIN
            user1.username = "u1"
            user1.password = "p1"
            user1.email = "email1@polito.it"
            user1.addRoleName(RoleName.ROLE_ADMIN)
            user1.isEnabled = true
            user1.name = "Pippo"
            user1.surname = "Franco"

            userRepository.save(user1).block()


            val user2 = User() //ADMIN
            user2.username = "u2"
            user2.password = "p2"
            user2.email = "email2@polito.it"
            user2.addRoleName(RoleName.ROLE_CUSTOMER)
            user2.isEnabled = true
            user2.name = "Marco"
            user2.surname = "Merola"
            user2.address = "Via Frinco 11, Torino"

            userRepository.save(user2).block()
        }

    }


    //Per salvare le password encoded nel DB
    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

}

fun main(args: Array<String>) {
    runApplication<CatalogServiceApplication>(*args)
}
