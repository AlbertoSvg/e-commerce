package it.polito.wa2.walletservice

import it.polito.wa2.saga.SagaApplication
import it.polito.wa2.walletservice.entities.Transaction
import it.polito.wa2.walletservice.entities.Wallet
import it.polito.wa2.walletservice.entities.WalletType
import it.polito.wa2.walletservice.enum.TransactionType
import it.polito.wa2.walletservice.repositories.TransactionRepository
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
        @Autowired walletRepository: WalletRepository,
        @Autowired transactionRepository: TransactionRepository
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

            var t : Transaction

            for(j in 0..10){
                t = Transaction().also {
                    it.amount = BigDecimal("20")
                    it.walletReceiver = w1
                    it.walletSender = w1
                    it.operationRef = "Recharge"
                    it.type = TransactionType.RECHARGE
                }
                transactionRepository.save(t)
            }

            for(i in 0..10){
                t = Transaction().also {
                    it.amount = BigDecimal("10")
                    it.walletReceiver = wallet
                    it.walletSender = w2
                    it.operationRef = "ORDER"
                    it.type = TransactionType.ORDER_PAYMENT
                }
                transactionRepository.save(t)
            }




        }

    }
}

fun main(args: Array<String>) {
    runApplication<WalletServiceApplication>(*args)
}
