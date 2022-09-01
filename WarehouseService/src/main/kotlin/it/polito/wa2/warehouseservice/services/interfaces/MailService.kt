package it.polito.wa2.warehouseservice.services.interfaces

interface MailService {

    fun sendMessage(toMail: String, subject: String, mailBody: String)

}
