package it.polito.wa2.saga.services.implementations

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.saga.entities.Emittable
import it.polito.wa2.saga.entities.OutboxEvent
import it.polito.wa2.saga.repositories.OutboxRepository
import it.polito.wa2.saga.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MessageServiceImpl: MessageService {

    private var objectMapper: ObjectMapper = Jackson2ObjectMapperBuilder.json().build()

    @Autowired
    private lateinit var outboxRepository: OutboxRepository

    override fun publish(message: Emittable, messageType:String, topic:String, persistInDb: Boolean){
        val outboxEvent = OutboxEvent(
            type = messageType ,
            destinationTopic = topic,
            payload = objectMapper.writeValueAsString(message),
            payloadId =message.getId()
        )

        val persistedEvent = outboxRepository.save(outboxEvent)
        if(!persistInDb)
            outboxRepository.delete(persistedEvent)

    }
}