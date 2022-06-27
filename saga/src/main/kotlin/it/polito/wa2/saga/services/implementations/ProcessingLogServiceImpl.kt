package it.polito.wa2.saga.services.implementations

import it.polito.wa2.saga.entities.ProcessedEvent
import it.polito.wa2.saga.repositories.ProcessedEventRepository
import it.polito.wa2.saga.services.ProcessingLogService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ProcessingLogServiceImpl: ProcessingLogService {

    @Autowired
    lateinit var processedEventRepository: ProcessedEventRepository

    override fun isProcessed(eventID: UUID) = processedEventRepository.findByIdOrNull(eventID) != null

    override fun process(eventID: UUID) {
        println(eventID)
        processedEventRepository.save(ProcessedEvent(eventID))
    }
}