package it.polito.wa2.catalogservice.gateway

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import it.polito.wa2.catalogservice.gateway.GatewayAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration


@Configuration
class GatewayConfig {

    @Autowired
    lateinit var filter: GatewayAuthenticationFilter

    @Bean
    fun routes(builder: RouteLocatorBuilder) : RouteLocator {
        return builder
            .routes()
            .route("WarehouseService - Products") {
                    it -> it.path(true,"/products/**")
                .filters { f ->
                    f.circuitBreaker { it ->
                        it.setFallbackUri("forward:/failure") //forward to local url failure1
                    }

                    //f.rewritePath("/warehouse", "/")
                    f.filter(filter)
                }

                .uri("lb://warehouse-service") //who im going to contact (lb = loadbalancing)
            }
            .route("WarehouseService - Warehouses") {
                    it -> it.path(true,"/warehouses/**")
                .filters { f ->
                    f.circuitBreaker { it ->
                        it.setFallbackUri("forward:/failure") //forward to local url failure1
                    }

                    //f.rewritePath("/warehouse", "/")
                    f.filter(filter)
                }

                .uri("lb://warehouse-service") //who im going to contact (lb = loadbalancing)
            }
            .route("WalletService") {
                    it -> it.path(true,"/wallets/**")
                .filters { f ->
                    f.circuitBreaker { it ->
                        it.setFallbackUri("forward:/failure") //forward to local url failure1
                    }

                    //f.rewritePath("/wallet", "/")
                    f.filter(filter)
                }
                .uri("lb://wallet-service") //who im going to contact (lb = loadbalancing)
            }
            .route("OrderService") {
                    it -> it.path(true,"/order/**")
                .filters { f ->
                    f.circuitBreaker { it ->
                        it.setFallbackUri("forward:/failure") //forward to local url failure1
                    }

                    f.rewritePath("/order", "/")
                    f.filter(filter)
                }

                .uri("lb://order-service") //who im going to contact (lb = loadbalancing)
            }
            .build()
    }

    @Bean //circuit breaker with Resilience4J
    fun defaultCustomizer() : Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        return Customizer { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(
                        CircuitBreakerConfig.ofDefaults()
                    ).timeLimiterConfig(
                                TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build()
                        )
                    .build()
            }
        }
    }
}
