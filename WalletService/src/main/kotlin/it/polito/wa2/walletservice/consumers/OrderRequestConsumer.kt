package it.polito.wa2.walletservice.consumers

import it.polito.wa2.saga.costants.Topics.paymentTopic
import it.polito.wa2.walletservice.dtos.order.request.WalletOrderRequestDTO
import it.polito.wa2.walletservice.services.OrderRequestService
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


    @KafkaListener( topics=[paymentTopic],
        containerFactory="orderRequestListenerFactory")
    fun listen(@Payload @Valid orderRequestDTO: WalletOrderRequestDTO,
               @Header("id") id: String,
               @Header("eventType") eventType:String
    ){
        println("Processing message $id ($eventType) : $orderRequestDTO")
        Thread.sleep(10000) //TODO: SLEEP DA RIMUOVERE
        orderProcessingService.process(orderRequestDTO,id)


    }
}