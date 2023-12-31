package it.polito.wa2.catalogservice.security

import it.polito.wa2.catalogservice.dtos.ErrorMessageDTO
import it.polito.wa2.catalogservice.gateway.GatewayAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebfluxSecurityConfig {

    @Bean
    fun reactiveAuthenticationManager(@Autowired userDetailsService: ReactiveUserDetailsService,
                                      @Autowired passwordEncoder: PasswordEncoder): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
        authenticationManager.setPasswordEncoder(passwordEncoder)
        return authenticationManager
    }


    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity,
                                  @Autowired authenticationWebFilter: JwtAuthenticationTokenFilter,
                                  @Autowired gatewayAuthenticationFilter: GatewayAuthenticationFilter
    ): SecurityWebFilterChain {

        http
             //auth
            .authorizeExchange()
            .pathMatchers("/auth/signin")
            .permitAll()
            .and()
            .authorizeExchange()
            .pathMatchers("/auth/register")
            .permitAll()
            .and()
            .authorizeExchange()
            .pathMatchers("/auth/registrationConfirm")
            .permitAll()
            .and()
            .authorizeExchange()
            .pathMatchers("/auth/enableUser")
            .hasRole("ADMIN")
            .and()
            .authorizeExchange()
            .pathMatchers("/auth/addRole")
            .hasRole("ADMIN")
            .and()
            .authorizeExchange()
            .pathMatchers("/auth/removeRole")
            .hasRole("ADMIN")

            // WalletService
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET,"/wallets/{walletId}") //CUSTOMERS CAN ONLY CALL THIS API (to GET his wallets)
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers("/wallets/**") //all the other apis are only for ADMINS
            .hasRole("ADMIN")

            //WarehouseService
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET,"/products/{productId}/warehouses")
            .hasRole("ADMIN")
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET,"/products/**")
            .permitAll()
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.POST,"/products/comments")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers("/products/**")
            .hasRole("ADMIN")

            .and()
            .authorizeExchange()
            .pathMatchers("/warehouses/**")
            .hasRole("ADMIN")

            //OrderService
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, "/orders/**")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, "/orders/{orderId}")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.POST, "/orders")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.DELETE, "/orders/{orderId}")
            .authenticated()
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.PATCH, "/orders/{orderId}")
            .authenticated()

        http
            .cors().disable()
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling()
            .authenticationEntryPoint(handler)
            .accessDeniedHandler(handler)

        http
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

        return http.build()
    }
}

private val handler = {
        swe: ServerWebExchange, e : Exception ->
    val body = ErrorMessageDTO(e.message, HttpStatus.UNAUTHORIZED)
    swe.response.statusCode = HttpStatus.UNAUTHORIZED
    swe.response.headers.contentType = MediaType.APPLICATION_JSON
    swe.response.writeWith(
        Jackson2JsonEncoder().encode(
            Mono.just(body),
            swe.response.bufferFactory(),
            ResolvableType.forInstance(body),
            MediaType.APPLICATION_JSON,
            Hints.from(Hints.LOG_PREFIX_HINT, swe.logPrefix)
        )
    )
}
