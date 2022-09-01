package it.polito.wa2.orderservice.config

import it.polito.wa2.orderservice.dtos.order.request.OrderDetailsDTO
import it.polito.wa2.saga.kafka.BaseKafkaConsumerConfig
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.stereotype.Component

@Component
class OrderDetailsConfig : BaseKafkaConsumerConfig<OrderDetailsDTO>(OrderDetailsDTO::class.java){

    @Bean
    fun orderDetailsListenerFactory(): ConcurrentKafkaListenerContainerFactory<String, OrderDetailsDTO>? {
        return super.concurrentKafkaListenerContainerFactory()
    }
}