package it.polito.wa2.catalogservice.gateway

import it.polito.wa2.catalogservice.security.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@RefreshScope
@Component
class GatewayAuthenticationFilter : GatewayFilter {

    @Autowired
    lateinit var jwtUtil: JwtUtils

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request: ServerHttpRequest = exchange.request

        val token = jwtUtil.parseJwt(request)
        if (token != null)
            populateRequestWithHeaders(exchange, token)
        return chain.filter(exchange)
    }

    private fun populateRequestWithHeaders(exchange: ServerWebExchange, token: String) {
        val details = jwtUtil.getDetailsFromJwtToken(token)
        val id = details?.getId()
        val roles  = details?.getRoles()

        exchange.request.mutate()
            .header("userId", id)
            .header("roles", roles)
            .build()
    }
}
