package it.polito.wa2.saga.services

import it.polito.wa2.saga.entities.Emittable


interface MessageService {

    fun publish(message: Emittable, messageType:String, topic:String, persistInDb: Boolean = true)
}