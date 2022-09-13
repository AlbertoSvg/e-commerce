package it.polito.wa2.catalogservice.consumers

import it.polito.wa2.catalogservice.dtos.MailDTO
import it.polito.wa2.catalogservice.services.MailServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import javax.validation.Valid

@Component
class MailConsumer {
    @Autowired
    lateinit var mailService: MailServiceImpl

    @KafkaListener(
        topics = ["mail"],
        containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    fun mailListener(@Payload @Valid mailDTO: MailDTO,
                             @Header("id") id: String,
                             @Header("eventType") eventType:String) {
        println("Processing message $id ($eventType) : $mailDTO")

        CoroutineScope(Dispatchers.IO).launch {
            mailService.sendMailToAdmins(mailDTO)
        }

    }
}