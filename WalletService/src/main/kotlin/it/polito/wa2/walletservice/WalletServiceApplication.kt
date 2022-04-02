package it.polito.wa2.walletservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class WalletServiceApplication{
    //TODO: POPULATE DB
}

fun main(args: Array<String>) {
    runApplication<WalletServiceApplication>(*args)
}
