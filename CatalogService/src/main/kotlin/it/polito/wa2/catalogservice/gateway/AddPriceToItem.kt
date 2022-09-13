package it.polito.wa2.catalogservice.gateway

import it.polito.wa2.catalogservice.dtos.*
import it.polito.wa2.catalogservice.security.JwtUtils
import it.polito.wa2.catalogservice.webclient.ClientRequest
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Component
class AddPriceToItem: RewriteFunction<OrderDTO, OrderRequestDTO> {

    @Autowired
    lateinit var webclient: ClientRequest

    override fun apply(t: ServerWebExchange?, u: OrderDTO): Publisher<OrderRequestDTO> {
        val userId : Long = t!!.request.headers.getFirst("userId")?.toLong()
            ?: throw RuntimeException("Error in AddPriceToItem")

        println("apply: $userId")
        return u.items
            .toFlux()
            .flatMap {item ->
                val uri = "http://warehouse-service:8200/products/${item.productId}"
                webclient.doGetReactive(uri, ResponseProductDTO::class.java).toMono().flatMap {
                    Mono.just(PurchaseProductDTO(
                        item.productId,
                        item.amount,
                        it.price
                    )
                    )
                }

            }.collectList()
            .map {
                OrderRequestDTO(
                    userId,
                    u.walletId,
                    u.deliveryAddress,
                    it
                )
            }
    }
}