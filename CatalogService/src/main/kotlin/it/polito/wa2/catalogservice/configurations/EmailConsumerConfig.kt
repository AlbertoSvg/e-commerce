package it.polito.wa2.catalogservice.configurations

import it.polito.wa2.catalogservice.dtos.MailDTO
import it.polito.wa2.catalogservice.kafka.BaseKafkaConsumerConfig
import org.springframework.stereotype.Component

@Component
class EmailConsumerConfig: BaseKafkaConsumerConfig<MailDTO>(MailDTO::class.java)