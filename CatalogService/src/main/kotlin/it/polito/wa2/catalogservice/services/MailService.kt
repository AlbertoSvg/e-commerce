package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dtos.MailDTO


interface MailService {
    suspend fun sendMailToCustomers(mail: MailDTO, id: String)

    suspend fun sendMailToAdmins(mail: MailDTO)
}
