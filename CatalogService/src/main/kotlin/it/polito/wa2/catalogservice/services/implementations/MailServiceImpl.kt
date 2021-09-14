package it.polito.wa2.catalogservice.services.implementations

import it.polito.wa2.catalogservice.services.interfaces.MailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class MailServiceImpl : MailService {

    @Autowired
    private lateinit var mailSender: JavaMailSender

    override fun sendMessage(toMail: String, subject: String, mailBody: String) {

        val message = SimpleMailMessage()

        message.setFrom("doNotReply@gmail.com")
        message.setTo(toMail)
        message.setSubject(subject)
        message.setText(mailBody)

        mailSender.send(message)

        println("Mail sent successfully to $toMail")

    }
}