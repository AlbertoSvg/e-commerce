package it.polito.wa2.warehouseservice.config

import it.polito.wa2.saga.kafka.BaseKafkaConsumerConfig
import it.polito.wa2.warehouseservice.dtos.order.request.WarehouseOrderRequestDTO
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.stereotype.Component


@Component
class OrderRequestConsumerConfig: BaseKafkaConsumerConfig<WarehouseOrderRequestDTO>(WarehouseOrderRequestDTO::class.java)

