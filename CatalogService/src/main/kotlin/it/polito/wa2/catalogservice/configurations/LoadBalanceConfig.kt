package it.polito.wa2.catalogservice.configurations

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class LoadBalanceConfig {

    @Bean
    @LoadBalanced
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
