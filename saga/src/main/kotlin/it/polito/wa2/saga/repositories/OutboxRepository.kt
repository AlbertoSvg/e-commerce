package it.polito.wa2.saga.repositories

import it.polito.wa2.saga.entities.OutboxEvent
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OutboxRepository: CrudRepository<OutboxEvent, UUID> {
}