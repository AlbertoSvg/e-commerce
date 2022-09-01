package it.polito.wa2.orderservice.config

import it.polito.wa2.orderservice.dtos.order.request.OrderStatusDTO
import it.polito.wa2.saga.kafka.BaseKafkaConsumerConfig
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.stereotype.Component

@Component
class OrderStatusConfig : BaseKafkaConsumerConfig<OrderStatusDTO>(OrderStatusDTO::class.java){

    @Bean
    fun orderStatusListenerFactory(): ConcurrentKafkaListenerContainerFactory<String, OrderStatusDTO>? {
        return super.concurrentKafkaListenerContainerFactory()
    }
}