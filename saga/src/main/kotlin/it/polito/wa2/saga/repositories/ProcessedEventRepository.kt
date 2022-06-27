package it.polito.wa2.saga.repositories

import it.polito.wa2.saga.entities.ProcessedEvent
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProcessedEventRepository: CrudRepository<ProcessedEvent, UUID> {
}