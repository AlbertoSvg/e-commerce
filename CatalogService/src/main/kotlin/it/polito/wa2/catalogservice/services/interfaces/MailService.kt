package it.polito.wa2.catalogservice.services.interfaces

interface MailService {

    /** ### Description:
     * Send a simple email message to a specific email address
     *
     * @param toMail The email address of the user (String)
     * @param subject The subject of the email (String)
     * @param mailBody The text to be sent (String)
     */
    fun sendMessage(toMail: String, subject: String, mailBody: String)

}
