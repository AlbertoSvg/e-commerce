package it.polito.wa2.saga.services

import java.util.*

interface ProcessingLogService  {

    fun isProcessed(eventID: UUID): Boolean

    fun process(eventID: UUID)

}