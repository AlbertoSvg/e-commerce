package it.polito.wa2.warehouseservice.consumers

import it.polito.wa2.saga.costants.Topics.orderRequestTopic
import it.polito.wa2.warehouseservice.dtos.order.request.WarehouseOrderRequestDTO
import it.polito.wa2.warehouseservice.services.interfaces.OrderRequestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import javax.validation.Valid


@Component
class OrderRequestConsumer{

    @Autowired
    lateinit var orderProcessingService: OrderRequestService


    @KafkaListener( topics=[orderRequestTopic], containerFactory="concurrentKafkaListenerContainerFactory")
    fun listen(@Payload @Valid orderRequestDTO: WarehouseOrderRequestDTO,
               @Header("id") id: String,
               @Header("eventType") eventType:String
    ){
        println("Processing message $id ($eventType) : $orderRequestDTO")
        orderProcessingService.process(orderRequestDTO,id)

    }
}