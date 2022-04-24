package it.polito.wa2.catalogservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class JwtAuthenticationTokenFilter : WebFilter {

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val jwt = jwtUtils.parseJwt(exchange.request)
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            val userDetailsDTO = jwtUtils.getDetailsFromJwtToken(jwt)
            if (userDetailsDTO != null) {
                val auth = UsernamePasswordAuthenticationToken(userDetailsDTO, null, userDetailsDTO.authorities)
                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
            }
        }
        return chain.filter(exchange)

    }

}
