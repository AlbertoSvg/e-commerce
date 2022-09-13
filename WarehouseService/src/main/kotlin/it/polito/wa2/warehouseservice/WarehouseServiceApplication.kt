package it.polito.wa2.warehouseservice

import it.polito.wa2.saga.SagaApplication
import it.polito.wa2.warehouseservice.entities.Category
import it.polito.wa2.warehouseservice.entities.Product
import it.polito.wa2.warehouseservice.entities.ProductStock
import it.polito.wa2.warehouseservice.entities.Warehouse
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import it.polito.wa2.warehouseservice.repositories.ProductStockRepository
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
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
    "it.polito.wa2.warehouseservice",
    "it.polito.wa2.saga"
],
    scanBasePackageClasses = [
        SagaApplication::class
    ])
@EnableEurekaClient
@EnableTransactionManagement
class WarehouseServiceApplication{

    @Bean
    fun createWarehousesAndProducts(
        @Autowired warehouseRepository: WarehouseRepository,
        @Autowired productRepository: ProductRepository,
        @Autowired productStockRepository: ProductStockRepository
    ): CommandLineRunner {
        return CommandLineRunner {

            val wh1 = Warehouse().also {
                it.name = "wh1"
            }

            warehouseRepository.save(wh1)

            val wh2 = Warehouse().also {
                it.name = "wh2"
            }

            warehouseRepository.save(wh2)


            //https://www.lg.com/it/images/tv/md05976376/gallery/lg-tv-43LK6100PLB_m001.jpg
//            val imageUrl = "https://www.lg.com/it/images/tv/md05976376/gallery/lg-tv-43LK6100PLB_m001.jpg"
//            val url = URL(imageUrl)
//            val stream: InputStream = url.openStream()
//            val byteArray = ByteArrayOutputStream()
//            val b = ByteArray(2048)
//            var length: Int
//            while (stream.read(b).also { length = it } != -1) {
//                byteArray.write(b, 0, length)
//            }
//            stream.close()
//
//            val picture = byteArray.toByteArray()

            val p1 = Product().also {
                it.name = "LG TV"
                it.description = "Home TV 4K"
                it.category = Category.TECH
                it.price = BigDecimal("1000")
                it.numRatings = 1
                it.numStars = 4
                //it.picture = picture
            }

            productRepository.save(p1)

            val p2 = Product().also {
                it.name = "A Room of One’s Own"
                it.description = "A Room of One’s Own - Virgina Woolf's book"
                it.category = Category.BOOKS
                it.price = BigDecimal("30")
                it.numRatings = 1
                it.numStars = 3
            }

            productRepository.save(p2)

            val p3 = Product().also {
                it.name = "Harry Potter and the Sorcerer's Stone"
                it.description = "First book of J. K. Rowling's saga"
                it.category = Category.BOOKS
                it.price = BigDecimal("24.20")
                it.numRatings = 2
                it.numStars = 9
            }

            productRepository.save(p3)

            val p4 = Product().also {
                it.name = "T-shirt H&M"
                it.description = "T-shirt"
                it.category = Category.CLOTHES
                it.price = BigDecimal("19.90")
                it.numRatings = 4
                it.numStars = 8
            }

            productRepository.save(p4)

            val stocks = listOf(
                ProductStock().also {
                    it.warehouse = wh1
                    it.product = p1
                    it.productQty = 10
                    it.alarmLevel = 5
                },
                ProductStock().also {
                    it.warehouse = wh1
                    it.product = p2
                    it.productQty = 100
                    it.alarmLevel = 50
                },
                ProductStock().also {
                    it.warehouse = wh1
                    it.product = p3
                    it.productQty = 9
                    it.alarmLevel = 10
                },

                ProductStock().also {
                    it.warehouse = wh2
                    it.product = p4
                    it.productQty = 5
                    it.alarmLevel = 5
                },
                ProductStock().also {
                    it.warehouse = wh2
                    it.product = p1
                    it.productQty = 20
                    it.alarmLevel = 10
                }
            )

            productStockRepository.saveAll(stocks)
        }
    }

}

fun main(args: Array<String>) {
    runApplication<WarehouseServiceApplication>(*args)
}
