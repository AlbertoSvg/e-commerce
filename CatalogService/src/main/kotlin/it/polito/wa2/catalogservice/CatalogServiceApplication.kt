package it.polito.wa2.catalogservice

import it.polito.wa2.catalogservice.configurations.EmailConfiguration
import org.springframework.beans.factory.annotation.Autowired
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
