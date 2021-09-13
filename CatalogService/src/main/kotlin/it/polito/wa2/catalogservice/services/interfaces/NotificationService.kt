package it.polito.wa2.catalogservice.services.interfaces


interface NotificationService {

    /** ### Description:
     * Generates and stores a random UUID token in the database
     * @param userName The username of the user (String)
     * @return String that contains the UUID token
     */
    fun createEmailVerificationToken(userName: String): String

}
