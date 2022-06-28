package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.entities.DatabaseSequence
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.util.*


@Service
class SequenceGeneratorService {

    val logger: Logger = LoggerFactory.getLogger(SequenceGeneratorService::class.java)

    @Autowired
    lateinit var mongoOperations: ReactiveMongoOperations


    fun generateSequence(seqName: String?): Long {
        return mongoOperations.findAndModify(
            Query(Criteria.where("_id").`is`(seqName)),
            Update().inc("sequence", 1), options().returnNew(true).upsert(true),
            DatabaseSequence::class.java
        ).doOnSuccess { `object` ->
            logger.debug(
                "databaseSequence is evaluated: {}",
                `object`
            )
        }.toFuture().get().sequence
    }
}