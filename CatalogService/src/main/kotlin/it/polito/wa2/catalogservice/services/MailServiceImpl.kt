package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.configurations.EmailConfiguration
import it.polito.wa2.catalogservice.dtos.MailDTO
import it.polito.wa2.catalogservice.utils.parseID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class MailServiceImpl: MailService {

    @Autowired
    private lateinit var mailSender: JavaMailSender

    @Autowired
    private lateinit var mailCfg: EmailConfiguration

    @Autowired
    lateinit var userDetailsServiceImpl: UserDetailsServiceImpl

    override suspend fun sendMailToCustomers(mail: MailDTO, id: String){


        if (mail.userEmail == null){
            mail.userEmail = userDetailsServiceImpl.getUserEmail(mail.userId.parseID())
        }

        val mailMessage = SimpleMailMessage()
        mailMessage.setSubject(mail.subject)
        mailMessage.setText(mail.mailBody)
        mailMessage.setTo(mail.userEmail)
        mailMessage.setFrom(mailCfg.username)

        mailSender.send(mailMessage)
        println("Mail sent successfully to ${mail.userEmail}")

    }

    override suspend fun sendMailToAdmins(mail: MailDTO){
        println("sendMailToAdmins")
        val adminEmails = userDetailsServiceImpl.getAdminsEmail()

        adminEmails.forEach {
            val mailMessage = SimpleMailMessage()
            mailMessage.setSubject(mail.subject)
            mailMessage.setText(mail.mailBody)
            mailMessage.setTo(it)
            mailMessage.setFrom(mailCfg.username)

            mailSender.send(mailMessage)
            println("Mail sent successfully to ${mail.userEmail}")
        }
    }

}

