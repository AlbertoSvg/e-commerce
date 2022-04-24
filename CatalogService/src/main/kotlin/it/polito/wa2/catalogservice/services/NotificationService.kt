package it.polito.wa2.catalogservice.services

import reactor.core.publisher.Mono

interface NotificationService {

    fun createEmailVerificationToken(userName: String): Mono<String>

}
