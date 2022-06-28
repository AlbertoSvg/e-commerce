package it.polito.wa2.catalogservice.entities

import it.polito.wa2.catalogservice.enum.Sequences
import it.polito.wa2.catalogservice.services.SequenceGeneratorService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component


@Component
class UserModelListener : AbstractMongoEventListener<User>() {
    val logger: Logger = LoggerFactory.getLogger(UserModelListener::class.java)

    @Autowired
    lateinit var sequenceGenerator: SequenceGeneratorService

    override fun onBeforeConvert(event: BeforeConvertEvent<User>) {
        try{
            if (event.source.id == null)
                event.source.id = (sequenceGenerator.generateSequence(Sequences.USER_SEQUENCE));
        } catch (e : Exception) {
            logger.error("Error:{}", e.message);
        }
    }
}