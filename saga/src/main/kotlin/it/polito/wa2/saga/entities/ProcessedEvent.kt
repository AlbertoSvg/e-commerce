package it.polito.wa2.saga.entities

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "processed_event")
class ProcessedEvent(
    @Id
    var eventId: UUID
)