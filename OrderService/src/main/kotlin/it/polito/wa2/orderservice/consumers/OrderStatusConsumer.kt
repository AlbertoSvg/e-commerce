package it.polito.wa2.orderservice.consumers

import it.polito.wa2.orderservice.dtos.order.request.EventTypeOrderStatus
import it.polito.wa2.orderservice.dtos.order.request.OrderStatusDTO
import it.polito.wa2.orderservice.services.interfaces.OrderService
import it.polito.wa2.saga.costants.Topics.orderStatusTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import javax.validation.Valid

@Component
class OrderStatusConsumer {

    @Autowired
    lateinit var orderService: OrderService

    @KafkaListener(topics=[orderStatusTopic], containerFactory = "orderStatusListenerFactory")
    fun listen(@Payload @Valid orderStatusDTO: OrderStatusDTO,
               @Header("id") id: String,
               @Header("eventType") eventType: String
    ){
        println("Processing message $id ($eventType) : $orderStatusDTO")
        orderService.processOrderCompletion(orderStatusDTO, id, EventTypeOrderStatus.valueOf(eventType))


    }
}