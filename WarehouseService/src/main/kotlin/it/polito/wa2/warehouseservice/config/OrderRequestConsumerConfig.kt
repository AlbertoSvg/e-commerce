package it.polito.wa2.warehouseservice.config

import it.polito.wa2.warehouseservice.dtos.order.request.WarehouseOrderRequestDTO
import it.polito.wa2.saga.kafka.BaseKafkaConsumerConfig
import org.springframework.stereotype.Component


@Component
class OrderRequestConsumerConfig: BaseKafkaConsumerConfig<WarehouseOrderRequestDTO>(WarehouseOrderRequestDTO::class.java)

