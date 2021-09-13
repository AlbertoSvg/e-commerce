package it.polito.wa2.catalogservice

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
import java.util.*

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
class CatalogServiceApplication {

    @Autowired
    lateinit var emailCfg: EmailConfiguration

    @Bean
    fun createCustomersAndUsers(
        @Autowired userRepository: UserRepository
    ): CommandLineRunner {
        return CommandLineRunner {

            val user1 = User() //ADMIN
            user1.username = "u1"
            user1.name = "Adams"
            user1.surname = "Scott"
            user1.address = "48 K St NW, Washington, DC 20001, US"
            user1.password = "p1"
            user1.email = "email1@polito.it"
            user1.addRoleName(RoleName.ROLE_ADMIN)
            user1.isEnabled = true

            userRepository.save(user1)

        }
    }

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
        props["mail.debug"] = "true"

        return mailSender
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()


}

fun main(args: Array<String>) {
    runApplication<CatalogServiceApplication>(*args)
}
