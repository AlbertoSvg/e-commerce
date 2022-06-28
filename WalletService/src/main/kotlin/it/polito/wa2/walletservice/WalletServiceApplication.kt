package it.polito.wa2.walletservice

import it.polito.wa2.saga.SagaApplication
import it.polito.wa2.walletservice.entities.Wallet
import it.polito.wa2.walletservice.entities.WalletType
import it.polito.wa2.walletservice.repositories.WalletRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.EnableTransactionManagement
import reactor.core.publisher.Mono
import java.math.BigDecimal

@SpringBootApplication(scanBasePackages = [
    "it.polito.wa2.walletservice",
    "it.polito.wa2.saga"
],
    scanBasePackageClasses = [
        SagaApplication::class
    ])
@EnableEurekaClient
@EnableTransactionManagement
class WalletServiceApplication{

    @Bean
    fun createEcommerceWallet(
        @Autowired walletRepository: WalletRepository
    ): CommandLineRunner {
        return CommandLineRunner {

            val wallet = Wallet()
            wallet.walletType = WalletType.ECOMMERCE
            wallet.owner = -1
            wallet.amount = BigDecimal("1000000")
            walletRepository.save(wallet)

            val w1 = Wallet()
            w1.walletType = WalletType.CUSTOMER
            w1.owner = 3
            w1.amount = BigDecimal("300")
            walletRepository.save(w1)

            val w2 = Wallet()
            w2.walletType = WalletType.CUSTOMER
            w2.owner = 4
            w2.amount = BigDecimal("0")
            walletRepository.save(w2)

        }

    }
}

fun main(args: Array<String>) {
    runApplication<WalletServiceApplication>(*args)
}
