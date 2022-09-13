package it.polito.wa2.orderservice

import it.polito.wa2.orderservice.constants.OrderStatus
import it.polito.wa2.orderservice.entities.Order
import it.polito.wa2.orderservice.entities.OrderItem
import it.polito.wa2.orderservice.repositories.OrderItemRepository
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.saga.SagaApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.math.BigDecimal
import java.util.*

@SpringBootApplication(scanBasePackages = [
    "it.polito.wa2.orderservice",
    "it.polito.wa2.saga"
],
    scanBasePackageClasses = [
        SagaApplication::class
    ])
@EnableEurekaClient
@EnableTransactionManagement
class OrderServiceApplication {
    @Bean
    fun createOrdersAndOrderItems(
        @Autowired orderRepository: OrderRepository,
        @Autowired orderItemRepository: OrderItemRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            val o1 = Order().also {
                it.deliveryAddress = "Democratic Republic of Congo"
                it.walletId = 1
                it.userId = 1
                it.orderStatus = OrderStatus.DELIVERING
            }
            val oi1 = OrderItem().also {
                it.order = o1
                it.productId = 1
                it.amount = 2
                it.price = BigDecimal(12.0)
                it.warehouseId = 1
            }

            val oi2 = OrderItem().also {
                it.order = o1
                it.productId = 2
                it.amount = 10
                it.price = BigDecimal(40.0)
                it.warehouseId = 2
            }
            orderRepository.save(o1)
            orderItemRepository.saveAll(
                listOf(
                    oi1,
                    oi2
                )
            )
        }
    }
}
fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
