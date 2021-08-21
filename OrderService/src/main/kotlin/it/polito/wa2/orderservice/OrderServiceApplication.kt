package it.polito.wa2.orderservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
